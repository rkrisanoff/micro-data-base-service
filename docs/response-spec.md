# Спека

# Table of contents

1. [User](#user)
    1. [Register](#register)
    2. [Login](#login)
2. [Group](#group)
    1. [createGroup](#createGroup)
    2. [enterGroup](#enterGroup)
    3. [quitGroup](#quitGroup)
    4. [deleteGroup](#deleteGroup)
    5. [getGroup](#getGroup)
3. [Queue](#queue)
    1. [createQueue](#createQueue)
    2. [deleteQueue](#deleteQueue)
    3. [enterQueue](#enterQueue)
    4. [quitQueue](#quitQueue)
    5. [getQueue](#getQueue)
    6. [getQAllQueues](#getQAllQueues)

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
  "accountCreds": {
    "login": "vasya"
  }
}
```

| Circuit                       | code | payload                                                              |
|-------------------------------|------|:---------------------------------------------------------------------|
| operation successful          | 0    | `{"id": 142857,"login":"login","password":"password",isAdmin:false}` |
| user with login doesn't exist | 1    | `{"login":"login"}`                                                  |

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

| Circuit                               | code | payload                                       |
|---------------------------------------|------|:----------------------------------------------|
| operation successful                  | 0    | `{}`                                          |
| user with userId doesn't exist        | 1    | `{"userId":"142857}`                          |
| user has already own group            | 2    | `{"userId":"142857,"groupName":"cool-group"}` |
| group with   inviteCode doesn't exist | 3    | `{"inviteCode": "cool-invite-code"}`          |

### quitGroup

```json
{
  "userId": 42
}
```

| Circuit                        | code | payload              |
|--------------------------------|------|:---------------------|
| operation successful           | 0    | `{}`                 |
| user with userId doesn't exist | 1    | `{"userId":"142857}` |
| user isn't in group            | 2    | `{"userId":"142857}` |

### deleteGroup

```json
{
  "userId": 42
}

```

| Circuit                        | code | payload              |
|--------------------------------|------|:---------------------|
| operation successful           | 0    | `{}`                 |
| user with userId doesn't exist | 1    | `{"userId":"142857}` |
| user isn't in group            | 2    | `{"userId":"142857}` |
| user isn't admin in group      | 3    | `{"userId":"142857}` |

### getGroup

```json
{
  "userId": 42
}
```

| Circuit                        | code | payload                                                                           |
|--------------------------------|------|:----------------------------------------------------------------------------------|
| operation successful           | 0    | `{"groupCreds":{"groupId":12,"groupName":"cool-name","inviteCode":"somestring"}}` |
| user with userId doesn't exist | 1    | `{"userId":"142857}`                                                              |
| user isn't in group            | 2    | `{"userId":"142857}`                                                              |
| user isn't admin in group      | 3    | `{"userId":"142857}`                                                              |

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
| user isn't in group            | 2    | `{"userId":"142857}` |
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
| queue with queeuId doens't exist | 4    | `{"queueId": 228}`   |

### enterQueue

```json
{
  "userId": 42,
  "queueId": 228
}
```

| Circuit                                    | code | payload                          |
|--------------------------------------------|------|:---------------------------------|
| operation successful                       | 0    | `{}`                             |
| user with userId doesn't exist             | 1    | `{"userId":"142857}`             |
| user isn't in group                        | 2    | `{"userId":"142857}`             |
| queue with queue in group Id doesn't exist | 4    | `{"queueId": 228,"groupId":227}` |
| user already in this queue                 | 5    | `{"userId": 42,"queueId": 228}`  |

### quitQueue

```json
{
  "userId": 42,
  "queueId": 228
}
```

| Circuit                                      | code | payload                                       |
|----------------------------------------------|------|:----------------------------------------------|
| operation successful                         | 0    | `{}`                                          |
| user with userId doesn't exist               | 1    | `{"userId":"142857}`                          |
| user isn't in group                          | 2    | `{"userId":"142857}`                          |
| queue with queueId in group Id doesn't exist | 4    | `{"queueId": 228,"groupId":227}`              |
| user isn't in queue in this queue            | 5    | `{"userId": 42,"queueId": 228,"groupId":227}` |

### getQueue

```json
{
  "userId": 42,
  "queueId": 228
}
```

| Circuit                                      | code | payload                                                                             |
|----------------------------------------------|------|:------------------------------------------------------------------------------------|
| operation successful                         | 0    | `{"studentsInQueueList":[{"fullName":"Artutrito Sanchez","gotInQueue":123442323}]}` |
| user with userId doesn't exist               | 1    | `{"userId":"142857}`                                                                |
| user isn't in group                          | 2    | `{"userId":"142857}`                                                                |
| queue with queueId in group Id doesn't exist | 4    | `{"queueId": 228,"groupId":227}`                                                    |

### getQAllQueues

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







