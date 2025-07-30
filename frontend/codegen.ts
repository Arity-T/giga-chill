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
    flattenArg: true
}

export default config