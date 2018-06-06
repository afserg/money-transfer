# money-transfer
Java test for Revolut

Runs as standalone application using wilfly swarm.

Provides REST api for money transfer between two accounts.

Uses H2 database as persistence storage. For the sake of this test data base has two initial accounts with numbers "123" and "234" and initial amount of 1000.

After start service available at http://localhost:8080/revolut/money/transfer

It accepts POST requests with JSON as a parameter and returns JSON result.

Request JSON format: {"from": "123", "to": "234", "amount": "12"}

Response JSON format: {
                          "success": true,
                          "errorMessage": null,
                          "accountFrom": {
                              "number": "123",
                              "amount": 988
                          },
                          "accountTo": {
                              "number": "234",
                              "amount": 1012
                          }
                      }