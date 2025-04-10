import org.gradle.kotlin.dsl.support.delegates.ProjectDelegate

fun ProjectDelegate.getPropertiesValue(
    keyName: String,
    propertiesFileName: String = "api.properties"
): String {
    val items = HashMap<String, String>()
    val fl = rootProject.file(propertiesFileName)
    (fl.exists()).let {
        fl.forEachLine {
            items[it.split("=")[0]] = it.split("=")[1]
        }
    }

    return items[keyName]!!
}