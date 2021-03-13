package com.example.eqmobilework

import com.example.eqmobilework.data.LocationEvent
import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test

import org.junit.Assert.*
import java.net.URL

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LibraryTest {
    @Test
    fun testSetup() {
        val classUnderTest = Library()
        assertTrue("setup should return 'true'", classUnderTest.setup())
    }

    @Test
    fun testLog() {
        val classUnderTest = Library()

        val client = mock<Client> {
            onGeneric { executeRequest(any()) } doReturn Response(
                statusCode = 200,
                responseMessage = "ok",
                url = URL("https://httpbin.org/post")
            )
        }

        FuelManager.instance.client = client

        assertTrue("Initial Requests should be empty", classUnderTest.requests.isEmpty())

        classUnderTest.log(
            LocationEvent(0F, 0F, System.currentTimeMillis() / 1000L, "empty"),
            false
        )

        assertTrue("Requests should not be empty", classUnderTest.requests.isNotEmpty())

        classUnderTest.process {
            assertTrue("Requests should be completed", classUnderTest.requests.isEmpty())
        }
    }

    @Test
    fun testFailedRequestsAreRetried() {
        val classUnderTest = Library()

        // mock failed response
        var client = mock<Client> {
            onGeneric { executeRequest(any()) } doReturn Response(
                statusCode = -1,
                responseMessage = "bad",
                url = URL("https://httpbin.org/post")
            )
        }

        FuelManager.instance.client = client

        assertTrue("Initial Requests should be empty", classUnderTest.requests.isEmpty())

        classUnderTest.log(
            LocationEvent(0F, 0F, System.currentTimeMillis() / 1000L, "empty"),
            false
        )

        classUnderTest.process {
            assertTrue("Request should fail", classUnderTest.requests.isNotEmpty())
        }

        // mock success response
        client = mock {
            onGeneric { executeRequest(any()) } doReturn Response(
                statusCode = 200,
                responseMessage = "ok",
                url = URL("https://httpbin.org/post")
            )
        }

        FuelManager.instance.client = client

        // process failed requests
        classUnderTest.process {
            assertTrue("Requests should be completed", classUnderTest.requests.isEmpty())
        }
    }
}