## EmailEventQueueFailure entity

```json
{
    "customerId": 1,
    "source": "Welcome"
}
```

Email event that could not be added to the table even after retries. 
We don't want to receive it, so we add this event to the table, 
which will then be processed by cron (cron is not implemented then).

Property: <b> source</b></br>
<sub>
    Type of email: 
     Welcome = registration welcome email
</sub>
