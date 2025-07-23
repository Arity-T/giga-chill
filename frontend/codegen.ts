import type { ConfigFile } from '@rtk-query/codegen-openapi'

// https://redux-toolkit.js.org/rtk-query/usage/code-generation#openapi
const config: ConfigFile = {
    apiFile: './src/store/api/api.ts',
    schemaFile: '../openapi/api.yml',
    apiImport: 'api',
    exportName: "codegenApi",
    argSuffix: 'Props',
    responseSuffix: "Response",
    outputFile: './src/store/api/codegenApi.ts',
    hooks: true,
    useEnumType: true,
    filterEndpoints: [
        'login',
        'register',
        'logout',
        'getMe',

        'getEvents',
        'createEvent',
        'getEvent',
        'updateEvent',
        'deleteEvent',

        'getInvitationToken',
        'createInvitationToken',
        'joinByInvitationToken',
    ],
    flattenArg: true
}

export default config