package com.example.teacherassistant.common

data class GroupsState(val groups: List<Group> = emptyList(), val error: String?= null)