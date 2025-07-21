import type { ConfigFile } from '@rtk-query/codegen-openapi'

// https://redux-toolkit.js.org/rtk-query/usage/code-generation#openapi
const config: ConfigFile = {
    schemaFile: '../openapi/api.yml',
    apiFile: './src/store/api/api.ts',
    apiImport: 'api',
    outputFile: './src/store/api/codegenApi.ts',
    exportName: 'codegenApi',
    hooks: true,
}

export default config