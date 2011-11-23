package cfg2metatype

import org.junit.Test
import org.junit.Before

/**
 * @Author Paul Bakker - paul.bakker.nl@gmail.com
 */
class Cfg2MetatypeFactoryConverterTest {
    def xml

    @Before
    public void before() {
        def resource = this.getClass().getClassLoader().getResource("org.amdatu.storage.servicefactory-aws.cfg")

        xml = new XmlParser().parseText(new Cfg2MetatypeConverter().convertFile(new File(resource.file)))
    }

    @Test
    void testHeadersCorrect() {
        assert xml.Designate[0].'@pid' == 'org.amdatu.storage.servicefactory-aws'
        assert xml.Designate[0].'@factoryPid' == 'org.amdatu.storage.servicefactory'
    }
}
