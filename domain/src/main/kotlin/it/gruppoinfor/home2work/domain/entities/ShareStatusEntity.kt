package it.gruppoinfor.home2work.domain.entities


enum class ShareStatusEntity constructor(val value: Int) {
    CREATED(0),
    COMPLETED(1),
    CANCELED(2)
}