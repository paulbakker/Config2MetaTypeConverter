package cfg2metatype

import groovy.xml.MarkupBuilder

/**
 * @Author Paul Bakker - paul.bakker.nl@gmail.com
 */
class Cfg2MetatypeConverter {
    public static void main(String[] args) {
        if (args.length == 0) {
            println("No input directory specified.")
        } else {
            new Cfg2MetatypeConverter().convert(new File(args[0]))
        }
    }

    void convert(File inputdir) {
        File outputDir = new File(inputdir.getAbsolutePath() + "/xml")
        outputDir.mkdir()

        inputdir.eachFileMatch({String name -> name.endsWith(".cfg")}, { File file ->
            String xml = convertFile(file)
            def writer = new FileWriter(new File(outputDir.absolutePath + "/" + file.name.replace(".cfg", ".xml")))
            writer.write(xml)
            writer.close()
        })
    }

    String convertFile(File file) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        FileReader reader = new FileReader(file)

        def properties = [:]
        reader.eachLine { String line ->
            if (!line.startsWith("#")) {
                def parts = line.split("=")
                def key = parts[0].trim()
                if (key.length() > 0) {
                    def value = parts.length > 1 ? parts[1].trim() : ""
                    properties.put(key, value)
                }
            }
        }


        xml.MetaData('xmlns:metatype': "http://www.osgi.org/xmlns/metatype/v1.0.0") {
            OCD(name: 'ocd', id: 'ocd') {
                properties.each {
                    AD(id: it.key, type: 'STRING', cardinality: 0)
                }
            }

            if (parsePidFromFileName(file).contains("-")) {
                Designate(pid: parsePidFromFileName(file), factoryPid: parseFactoryPidFromFileName(file)) {
                    Object(ocdref: 'ocd') {
                        properties.each { key, val ->
                            Attribute(adref: key) {
                                Value() {
                                    mkp.yieldUnescaped('<![CDATA[' + val + ']]>')
                                }
                            }
                        }
                    }
                }
            } else {
                Designate(pid: parsePidFromFileName(file)) {
                    Object(ocdref: 'ocd') {
                        properties.each { key, val ->
                            Attribute(adref: key) {
                                Value() {
                                    mkp.yieldUnescaped('<![CDATA[' + val + ']]>')
                                }
                            }
                        }
                    }
                }
            }


        }

        return writer.toString()

    }

    String parseFactoryPidFromFileName(File file) {
        return file.name.substring(0, file.name.indexOf("-"))
    }

    private String parsePidFromFileName(File file) {
        return file.name.substring(0, file.name.indexOf(".cfg"))
    }
}
