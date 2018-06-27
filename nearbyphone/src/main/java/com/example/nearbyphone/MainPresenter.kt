package com.example.kennyrizky.nearbyprint

import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

/**
 * Created by jurnal on 27/06/18.
 */

class MainPresenter : MainContract.Presenter {

    private val TAG = MainPresenter::class.simpleName
    private var mView : MainContract.View? = null
    private var mConnectionsClient : ConnectionsClient? = null
    private lateinit var mConnectedEndpoint: String
    private var counter = 0

    /**
     * Endpoint Discovery Callback
     */
    private val mDiscoveryCallback: EndpointDiscoveryCallback = object: EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpoint: String, endpointInfo: DiscoveredEndpointInfo) {
            Log.d(TAG, "Endpoint: $endpoint")
            Log.d(TAG, "Discovered Endpoint Info: ${endpointInfo.endpointName}")
            connectToEndpoint(endpointId = endpoint)
        }

        override fun onEndpointLost(endpoint: String) {
            Log.d(TAG, "Endpoint $endpoint lost")
        }
    }

    /**
     * Connection Lifecycle Callback
     */

    private val mConnectionLifecycleCallback: ConnectionLifecycleCallback = object: ConnectionLifecycleCallback() {
        override fun onConnectionResult(endpoint: String, result: ConnectionResolution) {
            Log.d(TAG, "Connection Result: From Endpoint $endpoint is ${
                when(result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        mConnectedEndpoint = endpoint
                        mConnectionsClient?.stopDiscovery()
                        Log.d(TAG, "Sending first payload...")
                        sendPayload()
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
            Log.d(TAG, "Connection initiated")
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

    override fun startDiscovery(context: Context) {
        mConnectionsClient = Nearby.getConnectionsClient(context).also {
            it.startDiscovery("com.kennyrizky.iot", mDiscoveryCallback, DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build())
            Log.d(TAG, "Discovery Started")
        }
    }

    override fun stopDiscovery() {
        mConnectionsClient?.stopDiscovery()
        Log.d(TAG, "Discovery Stopped")
    }

    /**
     * Private Method
     */
    private fun connectToEndpoint(endpointId: String) {
        mConnectionsClient?.requestConnection(Build.DEVICE, endpointId, mConnectionLifecycleCallback)
    }

    private fun sendPayload() {
        Handler().postDelayed({
            Log.d(TAG, "Sending Payload")
            mConnectionsClient?.sendPayload(mConnectedEndpoint, Payload.fromBytes("Counter Send From Mobile: ${++counter}".toByteArray()))
        }, 2000)
    }


}