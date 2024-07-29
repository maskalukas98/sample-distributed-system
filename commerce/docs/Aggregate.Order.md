## Order

```json
{
    "order": {
      "id": 1954919124830215,
      "productId": 268435457,
      "address": "Prague 11",
      "createdAt": "2023-10-26T18:32:12.941819"
    },
    "statuses": [{
          "status": "created",
          "isActive": false,
          "updatedAt": "2023-10-26T18:33:12.941819"
        },{
          "status": "delivering",
          "isActive": true,
          "updatedAt": "2023-10-26T18:35:12.941819"
    }]
}
```

How to create order?
1) Partner must be created at first step.
2) Partner must create new product.
3) Customer can create order for some specific product.

**Properties**:
- **order.id**: shard key
    - shard key for order described more in README.md
    
**Statuses**
- After some order is created, the status "created" is immediately added.