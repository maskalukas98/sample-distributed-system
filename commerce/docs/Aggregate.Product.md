## Order

```json
{
  "id": 268435457,
  "product": {
    "id": 268435457,
    "partnerId": "268435000",
    "productName": "Hamburger",
    "price": 10.2,
    "isActive": true,
    "currencyCode": "EUR"
  },
  "currency": {}
}
```


How to create order?
1) Partner must be created at first step.
2) New product can be created.


**Properties**:
- **product.id**: shard key
    - shard key for product described more in README.md
    
