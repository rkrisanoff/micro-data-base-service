package ifmo.dma.microdb.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class MResponse @JsonCreator constructor(
    @JsonProperty("successful") val successful: Boolean,
    @JsonProperty("error_message") val errorMessage: String?,
    @JsonProperty("response_code") val responseCode: Int,
    @JsonProperty("payload") val payload: Any?,
)
