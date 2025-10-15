// Wrapper script to run the demoScript shared library in standalone Groovy

// Create a GroovyShell with a shared binding
def binding = new Binding()
def shell = new GroovyShell(binding)

// Load utility scripts
shell.evaluate(new File('utils.groovy').text)
shell.evaluate(new File('yaml_util.groovy').text)

// Load demoScript and get the script object
def demoScriptObj = shell.evaluate(new File('demoScript.groovy').text)

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
    def result = demoScriptObj.call([
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
    e.printStackTrace()
    System.exit(1)
}
