package ifmo.dma.microdb.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class MRequest @JsonCreator constructor  (
    @JsonProperty("command") val command:String,
    @JsonProperty("payload") val payload:Any
)