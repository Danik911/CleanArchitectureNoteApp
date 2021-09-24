package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.util

sealed class OrderType{
    object Ascending: OrderType()
    object Descending: OrderType()
}
