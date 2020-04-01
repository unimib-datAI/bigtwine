import { SpyObject } from './spyobject';
import { BtwTrackerService } from 'app/core/tracker/tracker.service';

export class MockTrackerService extends SpyObject {
    constructor() {
        super(BtwTrackerService);
    }

    connect() {}
}
