import { defineConfig } from 'cypress'

export default defineConfig({
    e2e: {
        baseUrl: process.env.FRONTEND_URL ?? 'http://localhost:3001',
        env: {
            apiUrl: process.env.BACKEND_URL ?? 'http://localhost:3000',
        },
        watchForFileChanges: false,
        specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}',
        supportFile: 'cypress/support/e2e.ts',
        viewportWidth: 1280,
        viewportHeight: 720,
        video: false,
        screenshotOnRunFailure: true,
        defaultCommandTimeout: 5000,
        requestTimeout: 5000,
        responseTimeout: 5000,
        setupNodeEvents(on, config) {
            // https://github.com/archfz/cypress-terminal-report?tab=readme-ov-file#options
            const options = {
                printLogsToConsole: 'onFail',
                printLogsToFile: 'always',
                outputRoot: config.projectRoot + '/cypress/logs/',
                outputTarget: {
                    'cypress-logs.txt': 'txt',
                    'cypress-logs.json': 'json',
                    'cypress-logs.html': 'html',
                }
            };
            require('cypress-terminal-report/src/installLogsPrinter')(on, options)
        },
    },
}) 