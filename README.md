# money-transfer
Java test for Revolut

RESTful service for money transfer between accounts.

Runs as standalone application.

Uses H2 in-memory database as persistence storage.
For the sake of this test data base has two accounts "123" and "234" with initial amount of 1000 each.

After start service available at http://localhost:8081/money/transfers

It accepts POST requests with JSON as a parameter and returns JSON result.

Request JSON format: {"from": "123", "to": "234", "amount": 12}

Response JSON format: {
                          "code": 201,
                          "status": "success",
                          "message": "",
                          "data": {
                              "tarnsferId": 11
                          }
                      }