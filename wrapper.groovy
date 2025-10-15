// Wrapper script to run the demoScript shared library in standalone Groovy

// Create a shared binding
def binding = new Binding()

// Parse and instantiate utility scripts
GroovyShell shell = new GroovyShell(binding)

// Load utils.groovy and make it available as 'utils'
def utilsScript = shell.parse(new File('utils.groovy'))
binding.setVariable('utils', utilsScript)

// Load yaml_util.groovy and make it available as 'yaml_util'
def yamlUtilScript = shell.parse(new File('yaml_util.groovy'))
binding.setVariable('yaml_util', yamlUtilScript)

// Load and parse demoScript with the shared binding
def demoScriptText = new File('demoScript.groovy').text
Script demoScript = shell.parse(demoScriptText)

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
