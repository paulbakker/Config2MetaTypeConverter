package cfg2metatype

import org.junit.Test
import org.junit.Before

/**
 * @Author Paul Bakker - paul.bakker.nl@gmail.com
 */
class Cfg2MetatypeConverterTest {
    def xml


    @Before
    public void before() {
        def resource = this.getClass().getClassLoader().getResource("org.amdatu.opensocial.shindig.cfg")

        xml = new XmlParser().parseText(new Cfg2MetatypeConverter().convertFile(new File(resource.file)))
    }

    @Test
    void testHeadersCorrect() {
        assert xml.OCD != null
        assert xml.OCD.size() == 1
        assert xml.OCD[0].'@name' == 'ocd'
        assert xml.OCD[0].'@id' == 'ocd'
    }

    @Test
    void testAds() {
        assert xml.OCD[0].AD.size() == 52
        def ad = xml.OCD[0].AD.find { it.'@id' == 'shindig.content-rewrite.exclude-urls' }
        assert ad
        assert ad.'@id' == 'shindig.content-rewrite.exclude-urls'
        assert ad.'@type' == 'STRING'
        assert ad.'@cardinality' == '0'
    }

    @Test
    void testDesignate() {
        assert xml.Designate[0]
        assert xml.Designate[0].'@pid' == 'org.amdatu.opensocial.shindig'
        assert xml.Designate[0].Object[0]
        assert xml.Designate[0].Object[0].'@ocdref' == 'ocd'
        assert xml.Designate[0].Object[0].Attribute.size() == 52
        def attribute = xml.Designate[0].Object[0].Attribute.find { it.'@adref' == 'shindig.content-rewrite.exclude-urls' }
        assert attribute
        assert attribute.Value[0]
        assert attribute.Value[0].text() == ".*"

    }
}
