package com.flexcode.devspace.github.domain.repository

import com.flexcode.devspace.github.domain.model.Following
import com.flexcode.devspace.core.utils.Resource
import kotlinx.coroutines.flow.Flow

interface GetFollowingRepository {

     fun getFollowing(username: String?): Flow<Resource<List<Following>>>
}