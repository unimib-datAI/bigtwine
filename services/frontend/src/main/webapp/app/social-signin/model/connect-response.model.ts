export interface ConnectResponse {
    user: string;
    providerId: string;
    providerUserId: string;
    displayName: string;
    idToken?: string;
}
