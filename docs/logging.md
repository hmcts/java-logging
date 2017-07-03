# Logging

In a microservice architecture it becomes increasingly important to standardise on a mechanism and format for logging. The ELK stack has been selected to provide a central store and interface to logs from services within Reform. What is missing is a standard approach for every microservice to follow to allow for simple integration and minimal maintenance of the logging component going forward.

## JSON formatting

Microservices should format their logging output to be json only. This has the following benefits

- JSON logs make it much easier to pick out specific pieces of information from logs and makes it much easier to make changes to the format.
- We are using the ELK stack (Elasticsearch, LogStash and Kibana). This stack uses JSON formatted logs internally and life is much easier if logs are JSON formatted on the way in.

If JSON logging is not possible custom LogStash ‘grok’ filters need to be written to parse the log format. These filters can be hard to get right and have to be kept up to date if the log format is ever updated.

### Log data flow

![Data flow diagram](images/log-data-flow.png)

### Logging via Filebeat

Below is an example Ansible entry for getting app logs into Filebeat:

```yaml
- hosts: servers
  roles:
     - { role: filebeat,
         prospectors:
           - name: /var/log/messages
             input_type: log
             tags: "['test', 'more tags']"
             fields:
                team: divorce
                product: divorce
                service: petition store
                environment: dev
        }
```

### JSON Schema for error and event logs

```json
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "type": "object",
  "properties": {
    "timestamp": {
      "type": "string"
    },
    "rootRequestId": {
      "type": "string"
    },
    "requestId": {
      "type": "string"
    },
    "originRequestId": {
      "type": "string"
    },
    "type": {
      "type": "string"
    },
    "microservice": {
      "type": "string"
    },
    "level": {
      "type": "string"
    },
    "errorCode": {
      "type": "string"
    },
    "message": {
      "type": "string"
    },
    "source": {
      "type": "string"
    },
    "stackTrace": {
      "type": "string"
    },
    "responseTime": {
      "type": "integer"
    },
    "responseCode": {
      "type": "integer"
    },
    "tags": {
      "type": "array",
      "items": {
        "type": "string"
      }
    },
    "identity": {
      "sessionId": {
        "type": "string"
      },
      "clientIp": {
        "type": "string"
      },
      "idamUuid": {
        "type": "string"
      }
    },   
    "hostname": {
      "type": "string"
    },
    "docker": {
      "type": "object",
      "properties": {
        "name": {
          "type": "string"
        },
        "id": {
          "type": "string"
        },
        "image": {
          "type": "string"
        },
        "hostname": {
          "type": "string"
        }
      },
      "required": [
        "name",
        "id",
        "image",
        "hostname"
      ]
    }
  },
  "required": [
    "timestamp",
    "rootRequestId",
    "requestId",
    "originRequestId",
    "type",
    "microservice",
    "level",
    "message",
    "source",
    "hostname"
  ]
}
```

### Example Log entry

```json
{
  "timestamp": "2017-01-06T22:47:31.012+00:00",
  "rootRequestId": "ee45c86c-05e3-4a93-b078-424e0790fcc9",
  "requestId": "123e4567-e89b-12d3-a456-426655440000",
  "originRequestId": "ee45c86c-05e3-4a93-b078-424e0790fcc9",
  "type": "java",
  "microservice": "payment",
  "level": "ERROR",
  "errorCode": "PAY0001",
  "message": "Permission denied",
  "source": "uk.got.hmcts.payment.Card::add:30",
  "responseTime": 222,
  "responseCode": 403,
  "tags": [
    "payment",
    "security"
  ],
  "identity": {
    "sessionId": "f215faf9d88b7f0a881632ee22459ee452a296c808d261b6cc993d3a1fd0600e",
    "clientIp": "211.100.160.1",
    "idamUuid": "950ad09b-7cdf-46ff-82a7-065fa6048aca"
  },
  "hostname": "prod-az1-div-app-01",
  "docker": {
    "name": "/payment_api_1",
    "id": "2523e8b26e989b6b318d81eef4307f690111a414eca3ac1f5992367cabd70a73",
    "image": "payment:1.1.1",
    "hostname": "2523e8b26e98"
  }
}
```

