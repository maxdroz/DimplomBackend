package utils

import spark.Filter

class AddJSONHeader {
    companion object {
        val add = Filter{ req, res ->
            res.header("Content-Type", "application/json")
        }
    }
}