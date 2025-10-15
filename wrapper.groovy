// Wrapper script to run the demoScript shared library in standalone Groovy

// Load utility scripts by evaluating their content
evaluate(new File('utils.groovy').text)
evaluate(new File('yaml_util.groovy').text)
evaluate(new File('demoScript.groovy').text)

// Get parameters from environment
def team = System.getenv("TEAM") ?: "frontend"
def suite = System.getenv("SUITE") ?: "ui-tests"
def test = System.getenv("TEST") ?: "smoke-test"
def customGreeting = System.getenv("CUSTOM_GREETING")
def customEnvironment = System.getenv("CUSTOM_ENVIRONMENT")

def customParams = [:]
if (customGreeting) {
    customParams.greeting = customGreeting
}
if (customEnvironment) {
    customParams.environment = customEnvironment
}

// Call the demoScript
try {
    def result = call([
        team: team,
        suite: suite,
        test: test,
        customParams: customParams
    ])
    if (result && result.status) {
        println "✅ Demo script completed with status: ${result.status}"
    } else {
        println "✅ Demo script completed successfully"
    }
} catch (Exception e) {
    println "❌ Demo script execution failed: ${e.message}"
    System.exit(1)
}
