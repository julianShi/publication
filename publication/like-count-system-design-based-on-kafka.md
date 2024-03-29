# "Like" Count System Design Based on Kafka

In Instagram, users are allowed to click the "like" button on posts. Here are the business analysis of this task

- The like are used for ranking and recommodation
- The users do not actually want to search posts by his/her "like"
- The web app is distributed. 
- A post from a celebrity may attract "like" in a short time, which results in a traffic surge. 
- A user is allowed to like a post for not more than once. A user can dislike a post by clicking the "like" button again. 

Most designs use Redis for a good balance between consistency and availability. But if we do not care about the availability much, we can adopt Kafka message queue as a cheaper solution in this task. 

## Container Level Architecture Design

The following write and read architecture using Kafka to satisfy the above business requirements. 



![PlantUML Diagram](https://planttext.com/api/plantuml/png/LP114eCW34NtEKKkG6VQfOjUe8VeH8ZeQC4YZ8kEToz6nNR34FB_om-aem-O9WvN64dtP1dhuEVyW1_G_ZLwnovYIyuC7C1Gf9ecHaswPLW0teaJuuEQdg0WTF3kpq_M1hOKnL8WGzVpK_la8e1aG6NyxDJzc5U91FOArS1Dp5qPNc4QpKgK2bKQSqSe5MxCOikDBQ2Sg2wXi-qAEcELcwZFO3c1rprcqZ7kPybayf8BKFd46y1ftIEjCX3j6J7hlW00)



A brief explain of the above architecture. 

- The browser cache is used to show the user1 the "like" status and count. The browser cache may be inconsistent with the database storage. 
- A message is sent to the Kafka queue. 
- A cron job is consuming the queue message, aggregate, and save to database. 
- There is no time limit to write to the "like_count" table. So a write-write lock can be used when we write. 
- A CRUD service may query the "like_count" table for displaying to the user2.  

Let's break the pipeline down, and explain one by one. 

## Kafka Queue Design

The Kafka message body schema: 

```yaml
- post_id: int # this is the Kafka key
- user_id: int
- action: boolean # true means like, false means dislike
- time: timestamp
```

- We use post_id as the Kafka queue partition key. 
- A single thread is used in a consumer pod, to prevent concurrency issues. 

## Aggretation Root Design

DDD analysis. The post_id is the aggretation root. One post can be liked for multiple times. We need two classes. 

![PlantUML Diagram](https://planttext.com/api/plantuml/png/Iyv9B2vMyCdCJdNEByqhKQZcAiWlBedFJE5A1l6vQhcuaY5A4ajIYqj02XsJIpBpyv1KbXQd5e7eHB04I0l2l7nTNGojN000)

Consequently, we need two MySQL tables, where the post_id is the partition key. 

```sql
CREATE TABLE like_count (
  post_id INT PRIMARY KEY,
  count INT
);
```

```sql
CREATE TABLE latest_like_action (
  post_id INT,
  user_id INT,
  action BOOLEAN,
  PRIMARY KEY (post_id, user_id)
);
```

## Update Strategy

First, we aggregate the messages, and get the latest action for each post_id-user_id pair, before updating the `latest_like_action` table. This reduces the databaes I/O. 

We don't need to read before updating the entries in the lastes_like_action table. We simply overwrite the original entries. 

```java
List<Message> messages = consumer.poll(size = 100);
Map<PostUserKey, LatestLikeAction> latestActionMap = messages.stream()  
    .collect(Collectors.toMap(  
        m -> new PostUserKey(m.getPostId(), m.getUserId()), 
        m -> new LatestLikeAction(m.getPostId(), m.getUserId(), m.getAction()),
        // resolve the conflict, and keep the latest action
        (m1, m2) -> m1.getTime() >= m2.getTime() ? : m1, m2 
    ));
latestLikeActionRepo.save(latestActionMap.getValues());
consumer.commit();
```

Second, we aggregate the action counts of each mentioned posts. Row-level write-write locks are used to prevent other threads from writing to the same row. The lock conflict is rare because ideally, the messages of the same post will go through the same Kafka partition, and consumed by the same pod in lieu. 

```java
// calculate the incremental count of all posts
Map<Integer, Long> likeCountMap = latestActionMap.getKeys().stream()
  .filter(a -> a.getAction())
  .collect(Collectors.groupingByConcurrent(
    LatestLikeAction::getPostId,
    Collectors.counting()
  ));
Map<Integer, Long> dislikeCountMap = latestActionMap.getKeys().stream()
  .filter(a -> !a.getAction())
  .collect(Collectors.groupingByConcurrent(
    LatestLikeAction::getPostId,
    Collectors.counting()
  ));
Map<Integer, Long> incrementalLikeCountMap = Stream.concat(
    likeCountMap.entrySet().stream(), 
    dislikeCountMap.entrySet().stream()
  ).collect(Collectors.toMap(
    Map.Entry::getKey, 
    Map.Entry::getValue,
    (likeCount, dislikeCount) -> likeCount - dislikeCount
  ));

// read old count with row lock
Map<Integer, Long> oldLikeCountMap = likeCountRepo.readMapWithLock(latestActionMap.getValues());

// calculate the new count
Map<Integer, Long> incrementalLikeCountMap = Stream.concat(
    oldLikeCountMap.entrySet().stream(), 
    incrementalLikeCountMap.entrySet().stream()
  ).collect(Collectors.toMap(
    Map.Entry::getKey,
    Map.Entry::getValue,
    (old, incremental) -> old + incremental
  ));

// save the new count and release lock
likeCountRepo.saveMapAndReleaseLock(incrementalLikeCountMap);
```

## Summary

Walla! By now, we've accomplished the overall write-read pipeline. Redis is not needed in this design. 


