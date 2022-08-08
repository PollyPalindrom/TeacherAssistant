package com.example.common_module.di

import com.example.common_module.data.repository.Repository

interface CommonDeps {
    val repository: Repository
}