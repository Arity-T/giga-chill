import type { ConfigFile } from '@rtk-query/codegen-openapi'

// https://redux-toolkit.js.org/rtk-query/usage/code-generation#openapi
const config: ConfigFile = {
    apiFile: './src/store/api/api.ts',
    schemaFile: '../openapi/build/openapi.yml',
    apiImport: 'api',
    exportName: "codegenApi",
    argSuffix: 'Props',
    responseSuffix: "Response",
    outputFile: './src/store/api/codegenApi.ts',
    hooks: true,
    useEnumType: true,
    flattenArg: true,
    filterEndpoints: (_operationName, operationDefinition) => {
        return !operationDefinition.path.startsWith("/test-utils")
    }
}

export default config