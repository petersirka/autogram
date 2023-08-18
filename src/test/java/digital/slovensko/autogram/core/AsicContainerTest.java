package digital.slovensko.autogram.core;

import digital.slovensko.autogram.core.errors.MultipleOriginalDocumentsFoundException;
import digital.slovensko.autogram.core.errors.OriginalDocumentNotFoundException;
import digital.slovensko.autogram.util.AsicContainerUtils;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.InMemoryDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;

class AsicContainerTest {

    @Test
    void testGetOriginalFileXdc() {
        var asiceWithXdcOriginalFileContent = "UEsDBAoAAAgAALZj+laKIflFHwAAAB8AAAAIAAAAbWltZXR5cGVhcHBsaWNhdGlvbi92bmQuZXRzaS5hc2ljLWUremlwUEsDBBQACAgIALZj+lYAAAAAAAAAAAAAAAAMAAAAZG9jdW1lbnQueG1sxZTbctowEIZfRaPbDJIVSAoUk+GUtE1oKYEELhVrMQpGciVxSN+ml32OvFhlHCB0aGY6veildn+t/v127drFep6gJRgrtQoxIwFGoCItpIpDPBxcFsr4ol5bi6g66t60ueMtrRyXCgzyN5Wt+lSIp86lVUqFz5NYL4mdUQETGm211Guz5C5w4gOUEYYPaqOsOCg3eEohxDxNExlx551l99+jaMqNBRfmttBH4aVyIsEcN6AjChNt5rSRpuQK/Ks8acSgBPcvVzC623dd8T4OFHlzu7o2msKc223pt6q+ULFyd3m1WpFVkWgT09MgYNQ3e7upV5DKOq4i8K/bxcMjRK7+WS+ff6JUC64k1Og2XHOwdvWeFs8/lnyOnHYaqddSUqMbSY0eePLnV3xz2EMLIjdg+zAB4+cNYp8a3bZ3YdSWMVjXBTfVIsQLo6paiuopYeekXAqIn2DASJGUiA/hF/UdTxZ+fLTlVNCstN99uFbnw/5lvyx7w+vJ0xWU5s1xen1yeiYiJsf3i06I0cBwZTOujSTWRrrp/Bi/QT9H2O+0Ch50IWIlVcgiQZGd4frfTmuTImsrckq/d79n0jNg/bJtdjFHt0f05521icv2HP8Lxa+aPY5H7NP9k1h26ZcbapQ06XTGTJd9L7FJrz9qduKUfnvw3yu64Spe8NjfszOMuiAkb/taUm0cZRb9MTIydZvFH4wG/5l84vbo34Bc36uObC499nuq/wJQSwcImbqZaCoCAADbBAAAUEsDBBQACAgIALZj+lYAAAAAAAAAAAAAAAAaAAAATUVUQS1JTkYvc2lnbmF0dXJlczAwMS54bWy1V1l3qsoS/itZ5tGbMDqQleQsRgUFZFLgjaEFlElG5dcf1B0z7Jx7s9e990m6uqq6vurqqs/nv45JfNeAooyy9GWAPMKDO5B6mR+lwcvA0LmH6eCv12enjLwnk/RZTYuC1KnqApR3vWVaPp23XgZhVeVPEFQX0SOoyugxKwIIRglkCjXII/qI3A9en/3y6Wb9y9gvb6Zt2z622MUQhWEYggmo1/HLKLgf3PH+yyDyHyZjeDoaY1PUdV0cH4/xEe7CGIaO0Cnsgsn2/RDg8+k2uyxpJ83SyHPiqHOqHqUIqjDz78g4yIqoCpPvItDVcxAIpLL0Qx/Fg4fg6cNZAmPIaAB9xvIThxdvMP4G6SHJCnBflM5DGTroaPzLpQq2oOjTDy6Ai4cfQH5ABneGyr8M/MyrE5BWj/0R1zwwUQDK6g+j60+//xTT1cvaiWvwWlu47B9OXIUvAEfZUKEY8caTJ5s9S1GplkWodYCNJYrAxssz9MX4Irgh/IJXP+XgH6oIIWDs/nqnqyLLQVFFoPwF+v7o+KD8SZ6uKdELJy23WZGUn5f/ZTVAv7v+3yff8nZCpU0bdE2kC2SORg4zWfLzjhsnHh6lMjoOeXZFihUI/nPyod/fya2eLxaXCmzOXz/LbrpOOtfCl3CWjTjCkHwb06BpvFK8YuGr9rIKluujg05U+zBSUspMvWhOrTNhw6yYLV64XWvCxMhIYD1g66VRNqcEL47N5lRqtpVzBNRqmKvMqwqRFFjM4+Sg16g9ZkpTqqJwPWZxaTjcjmBzqGniVDcDFIpWTcx5BYrOMHhXOJkQePKCm292Rjfv4FwuGxFDobwLdlotOfg2oASWGFqR0sWu0NqTOpxv2TBqYQl0y/GuU4oDbws7S5mZ42A8N4+oc1DzYVswoyXdmUIghkoNiPV8tMUwCm8yScyzfBstslUWyoC06Vpkw4Yv6kM6zBNf5TFruKUn83RSC91SAsdyA+uSx81HBdatqZXZvrzcbur9ai63tQCn282ZI5hgnMq5LbTa3QGvkpwEvNLSi9B3wDut9kIn/pf8omVx3+/veP3i+qv6mwv6/M62fduswKvI8zTM0DTpRgHZ8hQZ8KwFQXbJkBIV7A/hPpoRLUyRSsmRDHkQVb5lSYtZK8qCIWPdRY+obfKBtlaOHENqVCCtKVIUaTgP3WQaGNh658y4UqRwk9H5TmTEVrz+HsU4O8tgkeFP8rusXexYTaTIGYkYLB20goESpZ+sTzwbG6KqtGxwOZ9hyGphbUYNz0mIhYahe8ah8S2jWMIis/mw8SRS6duXQjJB0L+f876S0f03RS59JR3P1EnDn3BTIjtIo1bKbhc3ni6M5YNI47HGqEAjGwFlCRnJ9yOutGw/mfj0UobhQNtYZuBuRRE2cazwSqtGArQ5WDgREcBUwpO42HczinAVWUbplamMeJ4roVSbMbPhLqoSy21LKLOhqcoEiqcn3g4vNETPCGINlNbaFk5DnUx3OjY6jDfiYrx1TyefmhvjcHd0bGhYC07cYbjOWTlxxIZHe85YQLbl4zwM4WWyhafsDEoOfh3Sy2mwlAkGixvX1ODZiDcxfqnlqGBr2j47yGXKC+aUPq6Wi8DQGjBzw12GbTexZUO4rhxl0uJUlk30pKshucvHJidNWVLF+/KCFRgi9hHRCrJBk311kPo3tUPKfe5Z0pssl6lrbbBkvkOH+orvwTGQPl1D5nqjOEJLpEc6tSXh6Dn8msAJvvXHzHIEaoadpzqX6Rm0m2okkJJ0l6bZNrHAQkH4tXA40CYOUROCYUuTHKOCkYgVFuYzw9GawjzpPJEJJq8eDsasHJHqNi9dpPan6tK3V2UJQ0W3EdNhkKg4MFLBPbRzDMmPwhDBVtNSXaWcIG4t3OWDZH86kRLMlHixJk5wo8QbDprs8VCQSiqwi07uq8a2JpTstsyQQ7TeZ9Oi64SDVLyI2RgD0yONq6E60rq4Xa04Hw7EuCGxNeNtZ8qoTZ2daqj7kYOH7JH2ZNwkRXIR7meIhx83hxhAjr+a5w3ue1NKJJVfveTr474Jrx0E+tJb5EtreH2+DNonpe5p1PbUk8P3UfyLyl0U/s0IPxNB7BHtyZzuFAGo+uH9s7F9PfkrAbjMpz+Z/h/d3Prpu7+PCj08Pep7IQqj2AM8eUDHOgI/ocQTjtvP0O96n00/Jfe6cxZ9/L5O5v8TT3BPTbIqiThcViCN+IN7gpaQnUQng+vyVeGHJ9jQpTJbHdvveMLvUV4lfFnWoNBAETnxbUpchT+dMx+0b5Pq4k+qExcUr8h4MpkgyIRA30fTx/234D6HAn3MMfTPVwH9QQUA//warrX/u8b7HtfzPae6uy5u9Kov7Z9y9zeXYl9HZw786uR5fA64/6MCNan/GGTNY7k/k3q/P9XL0sqJUlAMe8Ebopvtm+BrfF+wfw8N+v6d3Ta+e/rXdvHWIj4xlX753Z/G178BUEsHCGrQBIE/BwAAcQ4AAFBLAwQUAAgICAC2Y/pWAAAAAAAAAAAAAAAAFQAAAE1FVEEtSU5GL21hbmlmZXN0LnhtbI2QwU4DMQxEf2Xla5UscEJR0974AvgAK/EWi8SJNt4V5etJkVoWcenNI83MG3l//MxpWGluXMTDo32AgSSUyHLy8Pb6Yp7heNhnFJ6oqbseQ49Ju0kPyyyuYOPmBDM1p8GVShJLWDKJur9+9wO6qQ3/CTa0iROZnp7Pv95pSclU1HcP46YiU2Q0eq7kAWtNHFB75bhKtKSNbd8WDO2+uMJ4P+K63/b9d9JOZbXt4xKIqBiKKLLQvLs0dPL475eHb1BLBwi5PYSYwAAAAIUBAABQSwECCgAKAAAIAAC2Y/pWiiH5RR8AAAAfAAAACAAAAAAAAAAAAAAAAAAAAAAAbWltZXR5cGVQSwECFAAUAAgICAC2Y/pWmbqZaCoCAADbBAAADAAAAAAAAAAAAAAAAABFAAAAZG9jdW1lbnQueG1sUEsBAhQAFAAICAgAtmP6VmrQBIE/BwAAcQ4AABoAAAAAAAAAAAAAAAAAqQIAAE1FVEEtSU5GL3NpZ25hdHVyZXMwMDEueG1sUEsBAhQAFAAICAgAtmP6Vrk9hJjAAAAAhQEAABUAAAAAAAAAAAAAAAAAMAoAAE1FVEEtSU5GL21hbmlmZXN0LnhtbFBLBQYAAAAABAAEAPsAAAAzCwAAAAA=";
        var asiceWithXdcOriginalFile = new InMemoryDocument(Base64.getDecoder().decode(asiceWithXdcOriginalFileContent), null, MimeTypeEnum.ASICE);

        var actualOriginalXdcFileContent = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48eGRjOlhNTERhdGFDb250YWluZXIgeG1sbnM6eGRjPSJodHRwOi8vZGF0YS5nb3Yuc2svZGVmL2NvbnRhaW5lci94bWxkYXRhY29udGFpbmVyK3htbC8xLjEiPjx4ZGM6WE1MRGF0YSBDb250ZW50VHlwZT0iYXBwbGljYXRpb24veG1sOyBjaGFyc2V0PVVURi04IiBJZGVudGlmaWVyPSJodHRwOi8vZGF0YS5nb3Yuc2svZG9jL2Vmb3JtL0FwcC5HZW5lcmFsQWdlbmRhLzEuOSIgVmVyc2lvbj0iMS45Ij48R2VuZXJhbEFnZW5kYSB4bWxucz0iaHR0cDovL3NjaGVtYXMuZ292LnNrL2Zvcm0vQXBwLkdlbmVyYWxBZ2VuZGEvMS45IiB4bWxuczp4c2k9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hLWluc3RhbmNlIj48c3ViamVjdD5Ob3bDqSBwb2RhbmllPC9zdWJqZWN0Pjx0ZXh0PlBvZMOhdmFtIHRvdG8gbm92w6kgcG9kYW5pZS48L3RleHQ+PC9HZW5lcmFsQWdlbmRhPjwveGRjOlhNTERhdGE+PHhkYzpVc2VkU2NoZW1hc1JlZmVyZW5jZWQ+PHhkYzpVc2VkWFNEUmVmZXJlbmNlIERpZ2VzdE1ldGhvZD0idXJuOm9pZDoyLjE2Ljg0MC4xLjEwMS4zLjQuMi4xIiBEaWdlc3RWYWx1ZT0iL0N0bjBCOUQ3SEtuNlVSRlI4aVBVS2Z5R2U0bUJZcEsrMjVkYzFpWVd1RT0iIFRyYW5zZm9ybUFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvVFIvMjAwMS9SRUMteG1sLWMxNG4tMjAwMTAzMTUiPmh0dHA6Ly9zY2hlbWFzLmdvdi5zay9mb3JtL0FwcC5HZW5lcmFsQWdlbmRhLzEuOS9mb3JtLnhzZDwveGRjOlVzZWRYU0RSZWZlcmVuY2U+PHhkYzpVc2VkUHJlc2VudGF0aW9uU2NoZW1hUmVmZXJlbmNlIENvbnRlbnRUeXBlPSJhcHBsaWNhdGlvbi94c2x0K3htbCIgRGlnZXN0TWV0aG9kPSJ1cm46b2lkOjIuMTYuODQwLjEuMTAxLjMuNC4yLjEiIERpZ2VzdFZhbHVlPSJRbzFqWVgxSld5ZHZNL09ML3JuaXJwaGsxck0xejQxZlBSWEJFZ3AvcWJnPSIgTGFuZ3VhZ2U9InNrIiBNZWRpYURlc3RpbmF0aW9uVHlwZURlc2NyaXB0aW9uPSJUWFQiIFRyYW5zZm9ybUFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvVFIvMjAwMS9SRUMteG1sLWMxNG4tMjAwMTAzMTUiPmh0dHA6Ly9zY2hlbWFzLmdvdi5zay9mb3JtL0FwcC5HZW5lcmFsQWdlbmRhLzEuOS9mb3JtLnhzbHQ8L3hkYzpVc2VkUHJlc2VudGF0aW9uU2NoZW1hUmVmZXJlbmNlPjwveGRjOlVzZWRTY2hlbWFzUmVmZXJlbmNlZD48L3hkYzpYTUxEYXRhQ29udGFpbmVyPg==";
        var actualOriginalXdcFile = new InMemoryDocument(Base64.getDecoder().decode(actualOriginalXdcFileContent), "document.xml", AutogramMimeType.XML_DATACONTAINER);

        var xdcFileFromAsice = AsicContainerUtils.getOriginalDocument(asiceWithXdcOriginalFile);

        Assertions.assertEquals(actualOriginalXdcFile.getName(), xdcFileFromAsice.getName());
        Assertions.assertEquals(actualOriginalXdcFile.getMimeType(), xdcFileFromAsice.getMimeType());
    }

    @Test
    void testGetOriginalFileNoSigantures() {
        var asiceContent = "UEsDBBQAAAAIAFZ6EFeZuploLAIAANsEAAAMAAAAZG9jdW1lbnQueG1sxZTPctowEMZfRaNrBskiJA0UOwMhSVtCSwkkcFSsxSgxkiuJP+nb9NjnyItVthMIHZqZTg89avfT6tvfrt08Xc9TtARjpVYhZiTACFSshVRJiEfDi8oJPo2aaxE3xr2rDnf8TCvHpQKD/E1lGz4V4plzWYNS4fMk0UtiH6iAKY1ftNRr8+QmcOADlBGGd2qjvDgoN3zMIMQ8y1IZc+ed5fffo3jGjQUXlrbQR+GlcirB7DegYwpTbea0lWXkEvyrPG0loAT3L9cxutl2Xfc+dhRlc5u6Np7BnNuX0m9VfaZi5ebyarUiq0OiTUKrQcCob/a6qFeRyjquYvCv28XdPcQu+qyXTz9RpgVXEpr0Jdx0sHZRX4unH0s+R047jdRrKWnSQtKkO578+RXfEvbIgigN2AFMwfh5g9imxtedTRh1ZALW9cDNtAjxwqiGlqJRJeyYnNQC4icYMHJIasSH8LP6hqcLPz565lTQrnfefeiq49HgYnAi+6Pu9PESavP2JOseVI9EzOTkdnEeYjQ0XNmcaytNtJFuNt/HbzgoEQ7OzyoedCVmNVXJI8EhO8LR306rSJG1FSWl37vfMukbsH7Zil0s0W0R/XlnberyPcf/QvGrZveTMft0+yiWPfrliholTTZ7YKbHvtfYtD8Yt8+TjH67898ruuIqWfDE37MPGPVASN7xtaQqHOUW/TE2MnPF4g/Hw/9MPnVb9G9AjraqPZv7esU3v6foF1BLAwQUAAAACABXehBXuT2EmMAAAACFAQAAFQAAAE1FVEEtSU5GL21hbmlmZXN0LnhtbI2QwU4DMQxEf2Xla5UscEJR0974AvgAK/EWi8SJNt4V5etJkVoWcenNI83MG3l//MxpWGluXMTDo32AgSSUyHLy8Pb6Yp7heNhnFJ6oqbseQ49Ju0kPyyyuYOPmBDM1p8GVShJLWDKJur9+9wO6qQ3/CTa0iROZnp7Pv95pSclU1HcP46YiU2Q0eq7kAWtNHFB75bhKtKSNbd8WDO2+uMJ4P+K63/b9d9JOZbXt4xKIqBiKKLLQvLs0dPL475eHb1BLAwQUAAAAAABWehBXiiH5RR8AAAAfAAAACAAAAG1pbWV0eXBlYXBwbGljYXRpb24vdm5kLmV0c2kuYXNpYy1lK3ppcFBLAQIUABQAAAAIAFZ6EFeZuploLAIAANsEAAAMAAAAAAAAAAEAIAAAAAAAAABkb2N1bWVudC54bWxQSwECFAAUAAAACABXehBXuT2EmMAAAACFAQAAFQAAAAAAAAABACAAAABWAgAATUVUQS1JTkYvbWFuaWZlc3QueG1sUEsBAhQAFAAAAAAAVnoQV4oh+UUfAAAAHwAAAAgAAAAAAAAAAQAgAAAASQMAAG1pbWV0eXBlUEsFBgAAAAADAAMAswAAAI4DAAAAAA==";
        var asiceWithoutSignatures = new InMemoryDocument(Base64.getDecoder().decode(asiceContent), null, MimeTypeEnum.ASICE);

        Assertions.assertThrows(OriginalDocumentNotFoundException.class, () -> AsicContainerUtils.getOriginalDocument(asiceWithoutSignatures));
    }

    @Test
    void testGetOriginalFileNoOriginalFileInAsice() {
        var asiceContent = "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";
        var asiceWithoutOriginalFile = new InMemoryDocument(Base64.getDecoder().decode(asiceContent), null, MimeTypeEnum.ASICE);

        Assertions.assertThrows(OriginalDocumentNotFoundException.class, () -> AsicContainerUtils.getOriginalDocument(asiceWithoutOriginalFile));
    }

    @Test
    void testGetOriginalFileNoFileEntriesInManifest() {
        var asiceContent = "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";
        var asiceWithManifestFileWithoutFileEntries = new InMemoryDocument(Base64.getDecoder().decode(asiceContent), null, MimeTypeEnum.ASICE);

        var originalFile = AsicContainerUtils.getOriginalDocument(asiceWithManifestFileWithoutFileEntries);

        var actualOriginalFileMimeType = AutogramMimeType.XML_DATACONTAINER;

        Assertions.assertNotEquals(actualOriginalFileMimeType, originalFile.getMimeType());
    }

    @Test
    void testGetOriginalFileInvalidManifest() {
        var asiceContent = "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";
        var asiceWithInvalidManifestFile = new InMemoryDocument(Base64.getDecoder().decode(asiceContent), null, MimeTypeEnum.ASICE);

        var originalFile = AsicContainerUtils.getOriginalDocument(asiceWithInvalidManifestFile);

        var actualOriginalFileMimeType = AutogramMimeType.XML_DATACONTAINER;

        Assertions.assertNotEquals(actualOriginalFileMimeType, originalFile.getMimeType());
    }

    @Test
    void testGetOriginalFileNoMainfest() {
        var asiceContent = "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";
        var asiceWithoutManifestFile = new InMemoryDocument(Base64.getDecoder().decode(asiceContent), null, MimeTypeEnum.ASICE);

        var originalFile = AsicContainerUtils.getOriginalDocument(asiceWithoutManifestFile);

        var actualOriginalFileMimeType = AutogramMimeType.XML_DATACONTAINER;

        Assertions.assertNotEquals(actualOriginalFileMimeType, originalFile.getMimeType());
    }

    @Test
    void testGetOriginalFileManifestFileEntriesNoAttributes() {
        var asiceContent = "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";
        var asiceWithManifestFileWithNoFileEntryAttributes = new InMemoryDocument(Base64.getDecoder().decode(asiceContent), null, MimeTypeEnum.ASICE);

        var originalFile = AsicContainerUtils.getOriginalDocument(asiceWithManifestFileWithNoFileEntryAttributes);

        var actualOriginalFileMimeType = AutogramMimeType.XML_DATACONTAINER;

        Assertions.assertNotEquals(actualOriginalFileMimeType, originalFile.getMimeType());
    }

    @Test
    void testGetOriginalFileMultipleFilesInAsice() {
        var asiceContent = "UEsDBBQDAAAIAAdfElfy7qAyvAAAALcBAAAVAAAATUVUQS1JTkYvbWFuaWZlc3QueG1slZBNbgIxDEb3PcXI2yqTllUVEdj1BO0BrIwBS4kTTTxo4PSMkIBBsICdP/+8J3m5HlNs9tRXzuLhu/2ChiTkjmXr4f/v1/zAerVMKLyhqu5SNNOZ1Gv0MPTiMlauTjBRdRpcLiRdDkMiUXe/786ia5r5FzCzbTiSIdH+0Nx6Q4ymoO482BkiUcdo9FDIA5YSOaBOSLuXriWt3GLlYOjzyAXs6wqdcqujPjdN41FticjyLnTxMtU+PH/1cQJQSwMEFAAICAgAoFESVwAAAAAAAAAAAAAAABoAAABNRVRBLUlORi9zaWduYXR1cmVzMDAxLnhtbLVXWXeqyhL+K1nm0ZswI2YlOauZUQFBRPENmWWUQdBff1F3zLBz7s1e59637uqq6pq66uvnv7o0uTt4ZRXl2csAeYQHd17m5G6UBS+DpcE/UIO/Xp/tKnKe1sDlFosoyOy6Kb3qrpfMqqfz0csgrOviCYKaMnr06ip6zMsAgtExQkEH5BF9RO4Hr89u9XST/iXsVjfRtm0fW+wiiMIwDMFjqOdxqyi4H9xJ7ssgch9IeOuShEP5CIwgGEngKEZi+BgjiK3vopj/fonnSpmfX7aMneVZ5NhJdLLr3kvZq8PcvQNJkJdRHabfWWDoZyMQSOeYh96KBwfBs4czBcYQYgB99uUnCi/aYPzNpYc0L737srIfqtBGCfKXSt3zvbIPv3dxuHz4gcsPyOBuqUsvg9qr6se6q68xYKOg3/+hZf3N95/suWox7aTxXhU5LKjCGiMhkLQt4lE6jmmN3aYbAfc2lI0XKHuyXYNq85dn6IvwhXDz7ouvxrHw/qaCkDGM3V/zOS/zwivryKt+OXzf2a5X/SRG15AYpZ1Vfl6m1eftP6wE6HfV//vgO5UO81S1zFajkYl2wt5dxKXfbvfcVBVQonUMtpV92N0M8f8efOj3N3Kr5YvEpfoO59XPohvxOgwcHJqu46leTgRbPKamk5hO2LLm0bSHI33DTVfOONwwNCPvJpPxLgytDayEdMjNdpnC2dNqQcaTIqzUrT7bujNIEDVDWx1rbhXPSpGw4mQcSojOe2MdDCVredS7lpCGKGNrK85XHAXndkZmIwdWkBOTXHmzvJ36mUYQPtIomOTAChb5K4HNSUcsovxQuSN2Sme7EbmnorSuCDsdY26u0cepqjQcXc15d7W3m62cHLV84Vsb67hZjVRknEmdQhVMKC2O1qrDSUxXTS6cFJEvqZgOS6yFusNIX0x4wuXcQCjK0WxJwmPBdzg6oVtlKA0LmsAkbLfHeWrNQpbdTZqGj+dS6p5UKjEyJ3h5uWXqPTWXbE294y1zawIes3Zt3zaLZrvznFqxU+9Vfam8xK8uuT7X278Y5eXSJ6rKfazii/6vMm96mPNj8/u+WXuvsiSx4Y5hQEoGoJVoEEiSiTnwUluJSz1oWc2aTPONFB4cBWgcT2ugnZ24pUxLAkCWHOjkmSuYJ5chTg6qNQ5adfwJmHSgmDTIDTZTkq2wOdkrt9mstGaLTTKZxtesIZ1klutUg2vlk3yUk7ynyV9p//wuSaKlHVDoIN6HcSSMW5g++wGAygCNAudzJpj2aw4chguUPO0ciG7RQFTWGjJLTSZLgbA+cSlWWEJU8Y6os4W5Stt4A1gVqYcouWsQNh3NXR+OF9JCwCgu1LbrUYhWIN5DqtexVjPc6hMkNdSiEZMDutsXIKu54JTumMyDXapADkB3fe5kImngrjdaJW7zCMbRcUArQYGLw2qj2QVNT5ua9hSPc01O1YRMIAAzdLBxHe9HJ14JozVDN10XkOtJapQnBuBDxXLizTwiuZLmunjnr8enORjOj2Tb8cNkWeAEXdCNgEsrPkwbLat3FqTHVOsmMdp5410kT7ZzdYZRjcAdVCtnJkuTUDIkszBmu6YAQxIjeiGKnYWFXWqixEo97JaQLU7S+rgzWokFGqBzXEhkSYhk0J5z6XKtxstApoFPtUxrsaYOz/vEsCCYwTJbXXgmmtavJYFh+r225OlWpukgKOngXIdOz2tJ09aiaW0pglb+yMd95GNYoNNBEoRxQIeH8JxrjgYyA+ZOyweXu3UaUC1rSV/rqxW1y7lK0xbHC1XdricCVjOpJdMI20kzMmBazD94MoAFZrEXFtIWYzXubBMAeP+eNLq13NgLuvqQyyVf91cgG8AEepQepaGWUYesLUgs5GPlZLbYEdkvSZeHo7I96iEeL2Hgbq3CqXfCRK01cTzJiHWkbDUNtY4lEpRLa6FvNJf1prw1y001XYlgTKUTsQ4yARyIPFRHcViW2XCjrIxSn+NCsbaasXHMeLdF1tMpn6eKtiQRTozYeGJOFMIR6iWVErRo7g9gNs2wLWceh+peHc4AJWjiiJ7baqdUkrgAxokcLmVXLJI1xLsk5QFGGFo1VI1gYkONRNg3pagfURjVmUNfa8zdkjUidFH2dV6vjfVidEzUZLQ/zQ6gOcziXBlGkjSTG4MdjXLiaIYeZo1HZIqieIKf4AOn1Qqr3hrcx2Z2I17bJvSloaqXVvj6fEEXTz3CSSL/2KPhd/zxC7teGP4DbjkjX+wR7dGrYZeBV/eI5WdY5XrzV9RzGcp/Ank+qrkNkXd9Hxl694yo7/0ojGIPMPWAUAZMPSHYE4xsnqHf+T6Lfgru9eRM+ri+wpH/EzhKFpTURWMH2IJc+HC1P44AFyvBcqTOI07INoEUmrGwAdC3yPR3K68Uqaoar1x4ZWQnt6l4Jf7RcP0gcpvRF6VKk2698pXE4N4vbISSCELBOAWj+Ptg/sj4Zupnw6CPEYf+PjHQH9SD557fxvUl/M7xfsb3kNeu766bG8LsC/2nX5c3lXJfVedvwGvtdTVUJHaUvRl8O3ojfL3+i2vfWw59/6huB9+982tveOsHn7BYv/3uS/z6b1BLBwjm1p3nmwcAAE8PAABQSwMECgAACAAAoFESV4oh+UUfAAAAHwAAAAgAAABtaW1ldHlwZWFwcGxpY2F0aW9uL3ZuZC5ldHNpLmFzaWMtZSt6aXBQSwMEFAAICAgAoFESVwAAAAAAAAAAAAAAAAgAAAB0ZXN0LnR4dAvJL8lXyEpVKEktLuECAFBLBwjvDPUSDwAAAA0AAABQSwMECgMAAAAAIVESV+8M9RINAAAADQAAAAkAAAB0ZXN0Mi50eHRUb3RvIGplIHRlc3QKUEsBAj8DFAMAAAgAB18SV/LuoDK8AAAAtwEAABUAJAAAAAAAAAAggLSBAAAAAE1FVEEtSU5GL21hbmlmZXN0LnhtbAoAIAAAAAAAAQAYAADzUjm60dkBAPNSObrR2QEA81I5utHZAVBLAQIUABQACAgIAKBRElfm1p3nmwcAAE8PAAAaAAAAAAAAAAAAAAAAAO8AAABNRVRBLUlORi9zaWduYXR1cmVzMDAxLnhtbFBLAQIKAAoAAAgAAKBREleKIflFHwAAAB8AAAAIAAAAAAAAAAAAAAAAANIIAABtaW1ldHlwZVBLAQIUABQACAgIAKBRElfvDPUSDwAAAA0AAAAIAAAAAAAAAAAAAAAAABcJAAB0ZXN0LnR4dFBLAQI/AwoDAAAAACFRElfvDPUSDQAAAA0AAAAJACQAAAAAAAAAIIC0gVwJAAB0ZXN0Mi50eHQKACAAAAAAAAEAGAAAo40/q9HZAYCe812r0dkBgL39V6vR2QFQSwUGAAAAAAUABQB2AQAAkAkAAAAA";
        var asiceWithMultipleFiles = new InMemoryDocument(Base64.getDecoder().decode(asiceContent), null, MimeTypeEnum.ASICE);

        Assertions.assertThrows(MultipleOriginalDocumentsFoundException.class, () -> AsicContainerUtils.getOriginalDocument(asiceWithMultipleFiles));
    }
}
