export interface EventIdPathParam {
    params: Promise<{
        eventId: string;
    }>;
}

export interface InvitationTokenPathParam {
    params: Promise<{
        invitationToken: string;
    }>;
}