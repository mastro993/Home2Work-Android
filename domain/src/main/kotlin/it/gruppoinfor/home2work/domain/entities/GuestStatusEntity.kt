package it.gruppoinfor.home2work.domain.entities


enum class GuestStatusEntity constructor(value: Int) {
    JOINED(0),
    COMPLETED(1),
    CANCELED(2)
}