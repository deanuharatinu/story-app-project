package com.deanu.storyapp.common.data.api.model

interface ApiMapper<E, D> {
    fun mapToDomain(apiEntity: E): D
}