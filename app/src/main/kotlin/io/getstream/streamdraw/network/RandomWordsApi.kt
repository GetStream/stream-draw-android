/*
 * Copyright 2022 Stream.IO, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.streamdraw.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface RandomWordsApi {

    @GET("skribble_words.json")
    suspend fun getWordList(): List<String>

    companion object {
        private const val DATA_BASE_URL =
            "https://gist.githubusercontent.com/skydoves/b7a045f42e66a7a61fd850e566993c9d/raw/c671a08e5bad0296e30c182ace5113bf4f18bc71/"

        operator fun invoke(): RandomWordsApi {
            return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(DATA_BASE_URL)
                .build()
                .create(RandomWordsApi::class.java)
        }
    }
}
