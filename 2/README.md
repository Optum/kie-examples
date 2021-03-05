# kie-examples-2

This example was created to illustrate how dates are handled with a DMN between Java applications, KIE Server, and Kogito.
Date comparisons are a common real-world business rule, so it is worth exploring how this works.

## Background

The dmn-tck has an [example test case](https://github.com/dmn-tck/tck/blob/master/TestCases/compliance-level-3/0007-date-time/0007-date-time.dmn)
for working with date times within a DMN using FEEL expressions.

In the example, all the input nodes either have `number` or `string` as the data type.
However, a DMN also allows a `date` type to be specified.

## Example DMN

We started by creating an example DMN, [MyDMN](./src/main/resources/MyDMN.dmn).

The first FEEL expression in this DMN converts an input string, such as "2100-01-01", to a date using the FEEL function `date()` and runs a comparison.

```FEEL
date(InputDateStr) > date("2000-01-31")
```

When running from Java, we can set up the input as a String:

```java
DMNContext dmnContext = dmnRuntime.newContext();
dmnContext.set("InputDateStr", "2100-01-01");
```

The second FEEL expression is similar, but it uses a `date` type as an input.

```FEEL
InputDate > date("2000-01-31")
```

When running from Java, we can set up the input using `LocalDate`

```java
DMNContext dmnContext = dmnRuntime.newContext();
dmnContext.set("InputDate", LocalDate.of(2100, 1, 1));
```

If you are using another FEEL type, check the Drools implementation for Built-In Types [here](https://github.com/kiegroup/drools/blob/master/kie-dmn/kie-dmn-feel/src/main/java/org/kie/dmn/feel/lang/types/BuiltInType.java) to see which Java types are supported. 

Here is a current summary of supported types:

|FEEL type | Java type |
|----------|------------|
| Any      | Object |
| boolean  | Boolean |
| number   | java.lang.Number (Integer and others extend Number) |
| string   | java.lang.String |
| date     | java.time.LocalDate |
| date and time | java.time.ZonedDateTime, java.time.OffsetDateTime, java.time.LocalDateTime |
| time     | java.time.LocalTime, java.time.OffsetTime, java.time.temporal.TemporalAccessor |
| duration | java.time.chrono.ChronoPeriod |
| list     | java.util.List |
| context  |  java.util.Map |
| unary test | org.kie.dmn.feel.runtime.UnaryTest |
| range | org.kie.dmn.feel.runtime.Range |


## Limitations when running in KIE Server

When MyDMN is deployed to a KIE Server, there is currently no way to invoke it via the API.
We can only pass in string values with JSON, and there is no magic in KIE Server that would automatically convert it to a date type.
Because of this, only `Decision-1` from MyDMN can be run, and `Decision-2` output it `null`.
Here is the response we get from KIE Server.

```json
{
  "type": "SUCCESS",
  "msg": "OK from container 'DemoProject_1.0.0-SNAPSHOT'",
  "result": {
    "dmn-evaluation-result": {
      "messages": [
      ],
      "model-namespace": "https://kiegroup.org/dmn/_C3611C76-584B-4282-A6DA-4AF2F242929A",
      "model-name": "MyDMN",
      "decision-name": [
      ],
      "dmn-context": {
        "InputDate": "2021-01-01",
        "InputDateStr": "2021-01-01",
        "Decision-2": null,
        "Decision-1": true
      },
      "decision-results": {
        "_ACAADE90-6C1A-4257-8690-4B38D76E43B6": {
          "messages": [
          ],
          "decision-id": "_ACAADE90-6C1A-4257-8690-4B38D76E43B6",
          "decision-name": "Decision-1",
          "result": true,
          "status": "SUCCEEDED"
        },
        "_C90DB33E-0959-4B1A-AB9C-03E1FAE3F52A": {
          "messages": [
          ],
          "decision-id": "_C90DB33E-0959-4B1A-AB9C-03E1FAE3F52A",
          "decision-name": "Decision-2",
          "result": null,
          "status": "SUCCEEDED"
        }
      }
    }
  }
}
```


## Running the Java example

Clone the repo, and run:

```java
./gradlew test
```

## Running in KIE Server

First, start up the `jbpm-server-full` Docker image.

```shell
docker-compose up -d
```

Then, upload the DMN file, [MyDMN](./src/main/resources/MyDMN.dmn), to a new project named `DemoProject` and deploy it. 

Once deployed, you can execute the DMN by interfacing with the KIE Rest API.

```shell
curl --request POST \
  --url http://localhost:8080/kie-server/services/rest/server/containers/DemoProject_1.0.0-SNAPSHOT/dmn \
  --header "authorization: Basic "`echo -n wbadmin:wbadmin | base64` \
  --header 'content-type: application/json' \
  --header 'x-kie-contenttype: JSON' \
  --data '{
 "model-namespace": "https://kiegroup.org/dmn/_C3611C76-584B-4282-A6DA-4AF2F242929A",
 "model-name": "MyDMN",
 "dmn-context": {
  "InputDate": "2021-01-01",
  "InputDateStr": "2021-01-01"
 }
}'
```

`wbadmin:wbadmin` are the default credentials for a locally running `jbpm-server-full` container.

## Next steps

- Verify how dates are handled with Kogito
- Contribute a fix to KIE Server to automatically convert string input to a date when using the date type in an input node