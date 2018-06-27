package com.example.kennyrizky.nearbythings

import android.content.Context
import android.os.Handler
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

/**
 * Created by jurnal on 27/06/18.
 */

class MainPresenter(context: Context) : MainContract.Presenter {

    private val TAG = MainPresenter::class.simpleName
    private var mView : MainContract.View? = null
    private var mConnectionsClient : ConnectionsClient? = null
    private lateinit var mConnectedEndpoint: String
    private var counter = 0

    /**
     * Connection Lifecycle Callback
     */

    private val mConnectionLifecycleCallback: ConnectionLifecycleCallback = object: ConnectionLifecycleCallback() {
        override fun onConnectionResult(endpoint: String, result: ConnectionResolution) {
            Log.d(TAG, "Connection Result: From Endpoint $endpoint is ${
                when(result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        mConnectedEndpoint = endpoint
                        "Ok"
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> "Rejected"
                    else -> "Unknown Error"
                }
            }")
        }

        override fun onDisconnected(p0: String) {
            Log.d(TAG, "Connection disconnected for endpoint: $p0")
        }

        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.d(TAG, "Connection Initiated")
            Log.d(TAG, "Accepting Connection from ${connectionInfo.endpointName}")
            mConnectionsClient?.acceptConnection(endpointId, mPayloadCallback)
        }
    }

    /**
     * Payload Callback
     */
    private val mPayloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(p0: String, payload: Payload) {
            Log.d(TAG, "Payload received: ${String(payload.asBytes()!!)}")
            sendPayload()
        }

        override fun onPayloadTransferUpdate(p0: String, payloadTransferUpdate: PayloadTransferUpdate) {
            Log.d(TAG, "Transfer updated: ${payloadTransferUpdate.totalBytes}")
        }
    }

    /**
     * Contract Method
     */
    override fun detachView() {
        mView = null
        Log.d(TAG, "View Detached")
    }

    override fun attachView(view: MainContract.View) {
        mView = view
        Log.d(TAG, "View Attached")
    }

    /**
     * Initialization
     */
    init {
        mConnectionsClient = Nearby.getConnectionsClient(context).also {
            it.startAdvertising("Android IoT", "com.kennyrizky.iot", mConnectionLifecycleCallback, AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build())
        }
    }

    /**
     * Private Method
     */
    private fun sendPayload() {
        Handler().postDelayed({
            Log.d(TAG, "Sending Payload")
            mConnectionsClient?.sendPayload(mConnectedEndpoint, Payload.fromBytes("Counter Send From Things: ${++counter}".toByteArray()))
        }, 2000)
    }

}