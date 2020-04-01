import { SpyObject } from './spyobject';
import { RxStompService } from '@stomp/ng2-stompjs';

export class MockRxStompService extends SpyObject {
    constructor() {
        super(RxStompService);
    }
}
