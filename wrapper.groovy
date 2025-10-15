// Wrapper script to run the demoScript shared library in standalone Groovy

// First, evaluate the utility scripts in the current script's context
Eval.me(new File('utils.groovy').text)
Eval.me(new File('yaml_util.groovy').text)

// Load and parse demoScript as a class
def demoScriptText = new File('demoScript.groovy').text
GroovyShell shell = new GroovyShell(this.class.classLoader)
Script demoScript = shell.parse(demoScriptText)

// Set the binding so demoScript can access utils and yaml_util
demoScript.binding = this.binding

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

// Call the demoScript's call method
try {
    def result = demoScript.call([
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
