import { ActionTypes } from 'app/analysis/twitter-neel/store';

describe('TwitterNeelAction', () => {
    describe('unique action types', () => {
        it('should contain unique types', () => {
            const actionTypeValues = Object.keys(ActionTypes).map(k => ActionTypes[k]);
            expect(actionTypeValues.length).toBe(new Set(actionTypeValues).size);
        });
    });
});
