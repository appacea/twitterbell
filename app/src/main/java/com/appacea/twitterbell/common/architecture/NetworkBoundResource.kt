/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appacea.twitterbell.common.architecture


import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.appacea.twitterbell.data.tweet.network.NetworkResponse

/**
 * A custom NetworkBoundResource class (original taken from Android Architecture Source)
 *
 * This implementation will handle network calls wrapped in a NetworkResponse and uses LiveData for observables
 *
 **/
abstract class NetworkBoundResource<ResultType, RequestType>
@MainThread constructor() {
    private val appExecutors: AppExecutors = AppExecutors()
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        @Suppress("LeakingThis")
        val dbSource = loadFromDb()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val networkResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(newData))
        }
        result.removeSource(dbSource)
        result.addSource(networkResponse) { response ->
            response?.let {
                when (!response.isFailure) {
                    true ->{
                        response.body?.let {
                            appExecutors.diskIO().execute {
                                saveCallResult(it)
                                appExecutors.mainThread().execute {
                                    result.addSource(loadFromDb()) { newData ->
                                        newData?.let {
                                            setValue(Resource.success(newData))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    false->{
                        onFetchFailed()
                        response.message?.let {
                            result.addSource(dbSource) { newData ->
                                setValue(Resource.error(it, newData))
                            }
                        }
                    }
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<NetworkResponse<RequestType>>
}