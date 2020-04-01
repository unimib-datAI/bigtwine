export enum SocialSignInType {
    OAuth10 = 'oauth'
}

export interface ISocialSignInProvider {
    id: string;
    name: string;
    readonly type: SocialSignInType;
}

export class SocialSignInProvider implements ISocialSignInProvider {
    readonly id: string;
    readonly name: string;
    readonly type: SocialSignInType;

    constructor(id: string, name: string, type: SocialSignInType) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
