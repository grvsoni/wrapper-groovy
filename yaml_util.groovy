import groovy.json.JsonOutput
import org.yaml.snakeyaml.Yaml

// Jenkins shared library utility functions
def readYaml(String yaml) {
    println "The utility to print YAML"
    println "=============================="
    println "${yaml}"
    println "=============================="
}

// Default call method - not typically used but required for Jenkins
def call(Map args = [:]) {
    error("Utils is a utility library. Use yaml_util.methodName() to call specific methods.")
}
