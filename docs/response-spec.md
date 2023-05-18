
# Table of contents

- [Table of contents](#table-of-contents)
- [Specification](#specification)
  - [Protocol](#protocol)
  - [Request-Format](#request-format)
  - [Response-Format](#response-format)
  - [User](#user)
    - [register](#register)
    - [login](#login)
  - [Group](#group)
    - [createGroup](#creategroup)
    - [enterGroup](#entergroup)
    - [quitGroup](#quitgroup)
    - [deleteGroup](#deletegroup)
    - [getGroup](#getgroup)
    - [getGroupList](#getgrouplist)
  - [Queue](#queue)
    - [createQueue](#createqueue)
    - [deleteQueue](#deletequeue)
    - [enterQueue](#enterqueue)
    - [quitQueue](#quitqueue)
    - [getQueue](#getqueue)
    - [getQAllQueues](#getqallqueues)

# Specification

Спека общения между микросервисами, принимающими запросы от фронта (api-gateway) и микросервиса для связи с базой данных

## Protocol

## Request-Format

```json
{
    "command": "findUserByLogin",
    "payload":{
       ...
    }
}
```

## Response-Format

Поле `successful` используется для того, чтобы передать на api-gateway информации об ошибке формата сообщения

```json
{
    "successful":false,
    "errorMessage": "error-message",
    "responseCode":1234,
    "payload":{
        ...
    }
}
```



## User

### register

```json
{
  "login": "vasya",
  "password": "qwerty",
  "fullName": "Arturito Sanchez"
}
```

| Circuit                       | code | payload             |
|-------------------------------|------|---------------------|
| operation successful          | 0    | `{}`                |
| user with login already exist | 1    | `{"login":"login"}` |

### login

```json
{
    "login": "vasya"
}
```

| Circuit                       | code | payload                                                                                     |
|-------------------------------|------|:--------------------------------------------------------------------------------------------|
| operation successful          | 0    | `{"id": 142857,"login":"login","fullName":"cool-name","password":"password",isAdmin:false}` |
| user with login doesn't exist | 1    | `{"login":"login"}`                                                                         |

## Group

### createGroup

```json
{
  "userId": 3,
  "groupName": "qwerty"
}
```

| Circuit                        | code | payload                                       |
|--------------------------------|------|:----------------------------------------------|
| operation successful           | 0    | `{"id":100500,"inviteCode":"qwertyui"}`       |
| user with userId doesn't exist | 1    | `{"userId":"142857}`                          |
| user  has already own group    | 2    | `{"userId":"142857,"groupName":"cool-group"}` |

### enterGroup

```json
{
  "userId": 42,
  "inviteCode": "cool-invite-code"
}
```

| Circuit                             | code | payload                                       |
|-------------------------------------|------|:----------------------------------------------|
| operation successful                | 0    | `{}`                                          |
| user with userId doesn't exist      | 1    | `{"userId":"142857}`                          |
| user has already group              | 2    | `{"userId":"142857,"groupName":"cool-group"}` |
| group with inviteCode doesn't exist | 3    | `{"inviteCode": "cool-invite-code"}`          |

### quitGroup

```json
{
  "userId": 42
}
```

| Circuit                                                       | code | payload                          |
|---------------------------------------------------------------|------|:---------------------------------|
| operation successful                                          | 0    | `{}`                             |
| user with userId doesn't exist                                | 1    | `{"userId":"142857}`             |
| user is not in group                                          | 2    | `{"userId":"142857}`             |
| user is admin in the group. Use `deleteGroup` request instead | 2    | `{"userId":"142857,"groupId":1}` |

### deleteGroup

```json
{
  "userId": 42
}

```

| Circuit                        | code | payload                                      |
|--------------------------------|------|:---------------------------------------------|
| operation successful           | 0    | `{}`                                         |
| user with userId doesn't exist | 1    | `{"userId":"142857}`                         |
| user isn't in group            | 2    | `{"userId":"142857}`                         |
| user isn't admin in group      | 3    | `{"userId":"142857,"groupId":1,"adminId":1}` |

### getGroup

```json
{
  "userId": 42
}
```

| Circuit                        | code | payload                                                                          |
|--------------------------------|------|:---------------------------------------------------------------------------------|
| operation successful           | 0    | `{"groupId":12,"groupName":"cool-name","inviteCode":"somestring"}`               |
| user with userId doesn't exist | 1    | `{"userId":"142857}`                                                             |
| user isn't in group            | 2    | `{"userId":"142857}`                                                             |


### getGroupList

```json
{
  "userId": 42
}
```

| Circuit                        | code | payload                                                                   |
|--------------------------------|------|:--------------------------------------------------------------------------|
| operation successful           | 0    | `{"userList"[{"userId": 142857,"login":"login","fullName":"cool-name"}]}` |
| user with userId doesn't exist | 1    | `{"userId":"142857}`                                                      |
| user isn't in group            | 2    | `{"userId":"142857}`                                                      |

## Queue

### createQueue

```json
{
  "userId": 42,
  "queueName": "some-name"
}
```

| Circuit                        | code | payload              |
|--------------------------------|------|:---------------------|
| operation successful           | 0    | `{}`                 |
| user with userId doesn't exist | 1    | `{"userId":"142857}` |
| user isn't in group*           | 2    | `{"userId":"142857}` |
| user isn't admin in group      | 3    | `{"userId":"142857}` |

### deleteQueue

```json
{
  "userId": 42,
  "queueId": 228
}
```

| Circuit                          | code | payload              |
|----------------------------------|------|:---------------------|
| operation successful             | 0    | `{}`                 |
| user with userId doesn't exist   | 1    | `{"userId":"142857}` |
| user isn't in group              | 2    | `{"userId":"142857}` |
| user isn't admin in group        | 3    | `{"userId":"142857}` |
| queue with queueId doesn't exist | 4    | `{"queueId": 228}`   |

### enterQueue

```json
{
  "userId": 42,
  "queueId": 228
}
```

| Circuit                                   | code | payload                                      |
|-------------------------------------------|------|:---------------------------------------------|
| operation successful                      | 0    | `{}`                                         |
| user with userId doesn't exist            | 1    | `{"userId":"142857}`                         |
| user isn't in group                       | 2    | `{"userId":"142857}`                         |
| queue with queueId doesn't exist          | 3    | `{"queueId": 228}`                           |
| queue is not in group of user with userId | 4    | `{userId": 42,"groupId":227,"queueId": 228}` |
| user already in this queue                | 5    | `{"userId": 42,"queueId": 228}`              |

### quitQueue

```json
{
  "userId": 42,
  "queueId": 228
}
```

| Circuit                                      | code | payload                                      |
|----------------------------------------------|------|:---------------------------------------------|
| operation successful                         | 0    | `{}`                                         |
| user with userId doesn't exist               | 1    | `{"userId":"142857}`                         |
| user isn't in group                          | 2    | `{"userId":"142857}`                         |
| queue with queueId doesn't exist             | 3    | `{"queueId": 228}`                           |
| queue with queueId in group Id doesn't exist | 4    | `{"queueId": 228,"groupId":227}`             |
| user isn't in queue                          | 5    | `{"userId": 42,"queueId": 228}` |

### getQueue

```json
{
  "userId": 42,
  "queueId": 228
}
```

| Circuit                                   | code | payload                                                                                                      |
|-------------------------------------------|------|:-------------------------------------------------------------------------------------------------------------|
| operation successful                      | 0    | `{"queueId":123,"queueName":"cool-queue","users":[{"fullName":"Artutrito Sanchez","gotInQueue":123442323}]}` |
| user with userId doesn't exist            | 1    | `{"userId":"142857}`                                                                                         |
| user isn't in group                       | 2    | `{"userId":"142857}`                                                                                         |
| queue with queueId doesn't exist          | 3    | `{"queueId": 228}`                                                                                           |
| queue is not in group of user with userId | 4    | `{"queueId": 228,"groupId":227}`                                                                             |

### getAllQueues

```json
{
  "userId": 42
}
```

| Circuit                        | code | payload                                                            |
|--------------------------------|------|:-------------------------------------------------------------------|
| operation successful           | 0    | `{"queueList":[{"id":228,"queueName":"BLPS","recordsNumber":13}]}` |
| user with userId doesn't exist | 1    | `{"userId":"142857}`                                               |
| user isn't in group            | 2    | `{"userId":"142857}`                                               |








