import { InjectableRxStompConfig } from '@stomp/ng2-stompjs';
import { AuthServerProvider, WindowRef } from 'app/core';
import { SERVER_API_URL } from 'app/app.constants';

export function rxStompConfigFactory(authServerProvider: AuthServerProvider, $window: WindowRef): InjectableRxStompConfig {
    // const loc = $window.nativeWindow.location;
    let url;
    url = 'ws://' + SERVER_API_URL.replace(/^https?:\/\//, '') + 'websocket/analysis';
    const authToken = authServerProvider.getToken();
    if (authToken) {
        url += '?access_token=' + authToken;
    }
    return {
        // Which server?
        brokerURL: url,

        // How often to heartbeat?
        // Interval in milliseconds, set to 0 to disable
        heartbeatIncoming: 0, // Typical value 0 - disabled
        heartbeatOutgoing: 20000, // Typical value 20000 - every 20 seconds

        // Wait in milliseconds before attempting auto reconnect
        // Set to 0 to disable
        // Typical value 500 (500 milli seconds)
        reconnectDelay: 500,

        // Will log diagnostics on console
        // It can be quite verbose, not recommended in production
        // Skip this key to stop logging to console
        debug: (msg: string): void => {
            console.log(new Date(), msg);
        }
    };
}
