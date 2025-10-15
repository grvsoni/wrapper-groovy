/**
 * Jenkins Pipeline Step for Demo Script with YAML Configuration
 *
 * This step implements the sophisticated YAML-based configuration system
 * with hierarchical inheritance: Global → Team → Suite → Test → Custom Parameters
 *
 * Usage Examples:
 *   demoScript team: 'frontend', suite: 'ui-tests', test: 'smoke-test'
 *   demoScript team: 'backend', suite: 'unit-tests', test: 'service-test', customParams: [greeting: 'Custom Hello!']
 *
 * @param team - Team name (frontend, backend)
 * @param suite - Test suite name (ui-tests, api-tests, unit-tests, integration-tests)
 * @param test - Test name (smoke-test, regression-test, service-test, etc.)
 * @param customParams - Optional map of custom parameters to override defaults
 */
import groovy.json.JsonOutput
import org.yaml.snakeyaml.Yaml

def call(Map args) {
    // Validate required parameters
    def team = args.team
    def suite = args.suite
    def test = args.test
    def customParams = args.customParams ?: [:]

    if (!team || !suite || !test) {
        error("Missing required parameters. Usage: demoScript team: 'frontend', suite: 'ui-tests', test: 'smoke-test'")
    }

    println "=" * 60
    utils.customEcho "🚀 JENKINS SHARED LIBRARY: DEMO SCRIPT EXECUTION"
    println "=" * 60
    println "Team: ${team} | Suite: ${suite} | Test: ${test}"

    try {
        // 1. Load global configuration from shared library
        println "\n📚 Step 1: Loading global configuration from shared library..."
        def globalInfo = [:]
        try {
            def globalConfigText = new File("config/global.yml").text
            println "📄 Global config file loaded, parsing YAML..."
            yaml_util.readYaml(globalConfigText)
            Yaml yaml = new Yaml()
            def globalConfig = yaml.load(globalConfigText)
            globalInfo = globalConfig.GLOBAL_SETTINGS ?: [:]
            println "✅ Global configuration loaded successfully"
        } catch (Exception e) {
            println "⚠️ Warning: Could not load global config from shared library: ${e.message}"
            println "📝 Using default global configuration..."
            globalInfo = [
                greeting: "Hello",
                environment: "development",
                logLevel: "INFO",
                timeout: 30,
                enableNotifications: true,
                emailFrom: "jenkins@company.com",
                emailSubject: "Build Notification: \${appName}",
                outputFormat: "json",
                includeTimestamp: true
            ]
        }

        // 2. Load team configuration from repository
        utils.customEcho "\n👥 Step 2: Loading team configuration for: ${team}"
        def teamConfig = [:]
        try {
            // Repo is cloned, no checkout needed
            def teamConfigText = new File("teams/${team}/markup.yml").text
            Yaml yaml = new Yaml()
            teamConfig = yaml.load(teamConfigText)
            println "Team configuration: ${teamConfig}"
            println "✅ Team configuration loaded for ${team}"
        } catch (Exception e) {
            println "⚠️ Warning: Could not load team config for ${team}: ${e.message}"
            teamConfig = [common: [:]]
        }

        // 3. Extract configuration hierarchy
        println "\n⚙️ Step 3: Building configuration hierarchy..."
        def teamInfo = teamConfig.common ?: [:]
        teamInfo.remove("suites")

        def suiteInfo = [:]
        if (teamConfig.common?.suites?.get(suite)) {
            suiteInfo = teamConfig.common.suites.get(suite)
            suiteInfo.remove("tests")
            println "✅ Suite configuration found for ${suite}"
        } else {
            println "⚠️ Suite '${suite}' not found, using defaults"
        }

        def testInfo = [:]
        if (teamConfig.common?.suites?.get(suite)?.tests?.get(test)) {
            testInfo = teamConfig.common.suites.get(suite).tests.get(test)
            println "✅ Test configuration found for ${test}"
        } else {
            println "⚠️ Test '${test}' not found, using defaults"
        }

        // 4. Combine configurations (hierarchy: global < team < suite < test < custom)
        println "\n🔄 Step 4: Merging configuration hierarchy..."
        def config = [:]
        config.putAll(globalInfo)
        config.putAll(teamInfo)
        config.putAll(suiteInfo)
        config.putAll(testInfo)
        config.putAll(customParams)

        println "✅ Configuration hierarchy merged successfully"

        // 5. Display final configuration
        println "\n📋 Step 5: Final Configuration Summary:"
        println "  • App Name: ${config.appName ?: 'N/A'}"
        println "  • Environment: ${config.environment ?: 'N/A'}"
        println "  • Greeting: ${config.greeting ?: 'N/A'}"
        println "  • Build Tool: ${config.buildTool ?: 'N/A'}"
        println "  • Test Command: ${config.testCommand ?: 'N/A'}"
        println "  • Timeout: ${config.timeout ?: 'N/A'} seconds"
        println "  • Log Level: ${config.logLevel ?: 'N/A'}"

        // 6. Execute the test (simulated)
        println "\n🧪 Step 6: Executing Test..."
        println "Greeting: ${config.greeting}"
        println "Running: ${config.testCommand ?: 'default test command'}"
        println "Environment: ${config.environment}"
        println "✅ Test execution completed successfully!"

        // 7. Generate output
        println "\n📊 Step 7: Generating Output..."
        def output = [
            status: "SUCCESS",
            team: team,
            suite: suite,
            test: test,
            appName: config.appName,
            environment: config.environment,
            greeting: config.greeting,
            timestamp: new Date().format("yyyy-MM-dd HH:mm:ss")
        ]

        if (config.outputFormat == "json") {
            println "JSON Output:"
            def jsonOutput = JsonOutput.prettyPrint(JsonOutput.toJson(output))
            println jsonOutput
        }

        // 8. Send notifications
        if (config.enableNotifications) {
            println "\n📧 Step 8: Sending Notifications..."
            println "From: ${config.emailFrom}"
            println "To: ${config.emailTo ?: 'default@company.com'}"
            println "Subject: ${config.emailSubject?.replace('${appName}', config.appName ?: 'Demo App')}"
            println "Message: ${config.greeting} - Build completed successfully!"
            println "✅ Notification sent!"
        }

        println "\n" + "=" * 60
        println "🎉 DEMO SCRIPT EXECUTION COMPLETED SUCCESSFULLY!"
        println "=" * 60

        return [
            status: 'SUCCESS',
            config: config,
            output: output
        ]

    } catch (Exception e) {
        println "\n❌ Error during demo script execution: ${e.message}"
        println "=" * 60
        error("Demo script execution failed: ${e.message}")
    }
}
