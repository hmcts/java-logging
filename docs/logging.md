# Logging

In a microservice architecture it becomes increasingly important to standardise on a mechanism and format for logging. The ELK stack has been selected to provide a central store and interface to logs from services within Reform. What is missing is a standard approach for every microservice to follow to allow for simple integration and minimal maintenance of the logging component going forward.

## JSON formatting

Microservices should format their logging output to be json only. This has the following benefits

- JSON logs make it much easier to pick out specific pieces of information from logs and makes it much easier to make changes to the format.
- We are using the ELK stack (Elasticsearch, LogStash and Kibana). This stack uses JSON formatted logs internally and life is much easier if logs are JSON formatted on the way in.

If JSON logging is not possible custom LogStash ‘grok’ filters need to be written to parse the log format. These filters can be hard to get right and have to be kept up to date if the log format is ever updated.

### Log data flow

![alt text](images/log-data-flow.png Log data flow diagram)

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
