package de.themoep.minedown.adventure.tests;

/*
 * Copyright (c) 2020 Max Lee (https://github.com/Phoenix616)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParserTest {

    private void check(Component parsed, String[] contains) throws Throwable {
        String gson = GsonComponentSerializer.gson().serialize(parsed);
        System.out.println(gson);
        for (String contain : contains) {
            if (!gson.contains(contain)) {
                System.err.print("'" + contain + "' is not contained!\n\n");
                throw new Throwable("'" + contain + "' is not contained in parsed " + gson);
            }
        }
        System.out.print("\n\n");
    }

    private void parseReplaceFirst(String mineDownString, String[] replacements, String... contains) throws Throwable {
        Component parsed = replacements != null ? new MineDown(mineDownString).replaceFirst(true).replace(replacements).toComponent() : MineDown.parse(mineDownString);
        System.out.println(mineDownString);
        check(parsed, contains);
    }

    private void parse(String mineDownString, String[] replacements, String... contains) throws Throwable {
        Component parsed = replacements != null ? MineDown.parse(mineDownString, replacements) : MineDown.parse(mineDownString);
        System.out.println(mineDownString);
        check(parsed, contains);
    }

    private void parse(String mineDownString, String placeholder, Component replacement, String... contains) throws Throwable {
        Component parsed = new MineDown(mineDownString).replace(placeholder, replacement).toComponent();
        System.out.println(mineDownString);
        check(parsed, contains);
    }
    private void parse(String mineDownString, String placeholder1, Component replacement1, String placeholder2, Component replacement2, String... contains) throws Throwable {
        Component parsed = new MineDown(mineDownString)
                .replace(placeholder1, replacement1)
                .replace(placeholder2, replacement2)
                .toComponent();
        System.out.println(mineDownString);
        check(parsed, contains);
    }

    @Test
    public void testParseLegacy() {
        System.out.println("testParseLegacy");
        Assertions.assertAll(
                () -> parse("&lbold &oitalic &0not bold or italic but black!", null, "{\"extra\":[{\"extra\":[{\"extra\":[{\"bold\":true,\"text\":\"bold \"}],\"text\":\"\"},{\"extra\":[{\"bold\":true,\"italic\":true,\"text\":\"italic \"}],\"text\":\"\"},{\"extra\":[{\"color\":\"black\",\"text\":\"not bold or italic but black!\"}],\"text\":\"\"}],\"text\":\"\"}],\"text\":\"\"}"),
                () -> parse("&cRed &land bold!", null, "\"bold\":true", "\"color\":\"red\""),
                () -> parse("&cRed, &2Green and &rnot red nor green!", null, "{\"extra\":[{\"extra\":[{\"extra\":[{\"color\":\"red\",\"text\":\"Red, \"}],\"text\":\"\"},{\"extra\":[{\"color\":\"dark_green\",\"text\":\"Green and \"}],\"text\":\"\"},{\"extra\":[\"not red nor green!\"],\"text\":\"\"}],\"text\":\"\"}],\"text\":\"\"}"),
                () -> parse("&bTest \n&cexample.com &rstring!", null, "\"action\":\"show_text\"", "\"action\":\"open_url\"", "\"url\":\"http://example.com\"", "\\n"),
                () -> parse("&bTest \n&chttps://example.com &rstring!", null, "\"action\":\"show_text\"", "\"action\":\"open_url\"", "\"url\":\"https://example.com\"", "\\n"),
                () -> parse("&bTest &chttps://example.com/test?t=2&d002=da0s#d2q &rstring!", null, "\"action\":\"show_text\"", "\"action\":\"open_url\"", "\"url\":\"https://example.com/test?t=2&d002=da0s#d2q\"")
        );
    }

    @Test
    public void testParseEvents() {
        System.out.println("testParseEvents");
        Assertions.assertAll(
                () -> parse("[command](/example command)!", null, "\"action\":\"show_text\"", "\"action\":\"run_command\"", "\"command\":\"/example command\""),
                () -> parse("[command](blue /example command)!", null, "blue", "\"action\":\"show_text\"", "\"action\":\"run_command\"", "\"command\":\"/example command\""),
                () -> parse("##&eTest## [&blue&b__this__](https://example.com **Hover ??text??**) ~~string~~!", null, "\"action\":\"show_text\"", "\"action\":\"open_url\"", "yellow", "blue"),
                () -> parse("&e##Test## [__this \\&6 \\that__](blue /example command hover=**Hover ??text??**) ~~string~~!", null, "underlined", "\"action\":\"show_text\"", "blue", "\"action\":\"run_command\"", "hover"),
                () -> parse("[TestLink](https://example.com) [Testcommand](/command test  )", null, "\"action\":\"show_text\"", "\"action\":\"open_url\"", "\"url\":\"https://example.com\"", "\"action\":\"run_command\"", "/command test"),
                () -> parse("&b&lTest [this](color=green format=bold,italic,underlined https://example.com Hover & text) string!", null, "\"action\":\"show_text\"", "\"action\":\"open_url\"", "\"url\":\"https://example.com\"", "\"bold\":true", "\"italic\":true", "\"underlined\":true", "\"color\":\"green\""),
                () -> parse("&bTest [this](color=green format=bold,italic,underline suggest_command=/example command hover=Hover text) string!", null, "\"action\":\"show_text\"", "\"action\":\"suggest_command\"", "\"command\":\"/example command\"", "\"bold\":true", "\"italic\":true", "\"underlined\":true", "\"color\":\"green\""),
                () -> parse("&b[Test] [this](6 bold italic https://example.com) &as&bt&cr&di&en&5g&7!", null, "\"action\":\"show_text\"", "\"action\":\"open_url\"", "\"url\":\"https://example.com\"", "\"bold\":true", "\"italic\":true", "\"color\":\"gold\""),
                () -> parse("&bTest [[this]](https://example.com)!", null, "\"action\":\"show_text\"", "\"action\":\"open_url\"", "\"url\":\"https://example.com\"", "[this]"),
                () -> parse("&bTest [**[this]**](https://example.com)!", null, "\"action\":\"show_text\"", "\"action\":\"open_url\"", "\"url\":\"https://example.com\"", "\"bold\":true"),
                () -> parse("[Test insertion](insert={text to insert} color=red)", null, "\"insertion\":\"text to insert\"", "\"color\":\"red\"")
                //() -> parse("[Test dialog click](show_dialog=minedown:my/custom/dialog)"),
        );
        Assertions.assertThrows(IllegalArgumentException.class, () -> MineDown.parse("&bTest [this](color=green format=green,bold,italic https://example.com) shit!"));
    }

    @Test
    public void testParseEscaping() {
        System.out.println("testParseEscaping");
        Assertions.assertAll(
                () -> parse("Test inner escaping [\\]](gray)", null, "\"text\":\"]\"", "\"color\":\"gray\"")
        );
    }

    @Test
    public void testParseCustomPayload() {
        System.out.println("testParseCustomPayload");
        Assertions.assertAll(
                () -> parse("[Test custom payload click](custom=my-custom-payload payload={custom-payload-value})", null, "\"action\":\"custom\"", "\"id\":\"minecraft:my-custom-payload\"", "\"payload\":\"custom-payload-value\""),
                () -> parse("[Test custom payload click](custom=my-custom-payload payload={custom-payload-value\\})", null, "\"action\":\"custom\"", "\"id\":\"minecraft:my-custom-payload\"", "\"payload\":\"{custom-payload-value}\""),
                () -> parse("[Test custom payload click](custom=my-custom-payload payload=\\{custom-payload-value})", null, "\"action\":\"custom\"", "\"id\":\"minecraft:my-custom-payload\"", "\"payload\":\"{custom-payload-value}\""),
                () -> parse("[Test custom payload click](custom=my-custom-payload payload={{custom-payload-value})", null, "\"action\":\"custom\"", "\"id\":\"minecraft:my-custom-payload\"", "\"payload\":\"{custom-payload-value\""),
                () -> parse("[Test custom payload click](custom=my-custom-payload payload={{custom-payload}-value})", null, "\"action\":\"custom\"", "\"id\":\"minecraft:my-custom-payload\"", "\"payload\":\"{custom-payload}-value\""),
                () -> parse("[Test custom payload click](custom=my-custom-payload payload={custom-{payload}-value})", null, "\"action\":\"custom\"", "\"id\":\"minecraft:my-custom-payload\"", "\"payload\":\"custom-{payload}-value\""),
                () -> parse("[Test custom payload click](custom=my-custom-payload payload={custom \\{payload\\} value})", null, "\"action\":\"custom\"", "\"id\":\"minecraft:my-custom-payload\"", "\"payload\":\"custom {payload} value\""),
                () -> parse("[Test custom payload click](custom=my-custom-payload payload={{custom-payload-value}})", null, "\"action\":\"custom\"", "\"id\":\"minecraft:my-custom-payload\"", "\"payload\":\"{custom-payload-value}\"")
        );
    }

    @Test
    public void testParseHexColors() {
        System.out.println("testParseHexColors");
        Assertions.assertAll(
                () -> parse("##&eTest## [&#593&b__this__](Text) ~~string~~!", null, "\"color\":\"#559933\""),
                () -> parse("##&eTest## [&#593593&b__this__](Text) ~~string~~!", null, "\"color\":\"#593593\""),
                () -> parse("##&eTest## [__this \\&6 \\that__](#290329 /example command hover=**Hover ??text??**) ~~string~~!", null, "\"color\":\"#290329\"", "run_command", "show_text"),
                () -> parse("##&eTest## [__this \\&6 \\that__](color=#290329 /example command hover=**Hover ??text??**) ~~string~~!", null, "\"color\":\"#290329\"", "run_command", "show_text")
        );
        Assertions.assertThrows(IllegalArgumentException.class, () -> MineDown.parse("&bTest [this](color=green format=green,bold,italic https://example.com) shit!"));
    }

    @Test
    public void testParseShadowColors() {
        System.out.println("testParseShadowColors");
        Assertions.assertAll(
                () -> parse("[Text with shadow](shadow=red)", null, "shadow_color", "1694455125"),
                () -> parse("[Text with shadow](shadow=c)", null, "shadow_color", "1694455125"),
                () -> parse("[Text with shadow](shadow=#f00)", null, "shadow_color", "1694433280"),
                () -> parse("[Text with shadow](shadow=#ff0000)", null, "shadow_color", "1694433280"),
                () -> parse("[Text with shadow](shadow=#f004)", null, "shadow_color", "1157562368"),
                () -> parse("[Text with shadow](shadow=#ff000044)", null, "shadow_color", "1157562368")
        );
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MineDown.parse("[Text with shadow](shadow=bold)"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MineDown.parse("[Text with shadow](shadow=)"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MineDown.parse("[Text with shadow](shadow=#12345)"));
    }

    @Test
    public void testParseGradient() {
        System.out.println("testParseGradient");
        Assertions.assertAll(
                () -> parse("[Test Gradient](#fff-#000) &7:D", null, "color", "white", "black", "gray"),
                () -> parse("[Test Gradient](#fff-#666666-#555555) &7:D", null, "color", "white", "#666666", "#585858", "gray"), // lerping might not end up exactly at the end point. this is okay.
                () -> parse("[Test Gradient](#fff-#666666-#fff) &7:D", null, "color", "white", "#666666", "white", "gray"),
                () -> parse("[Test Gradient](#fff-#000 Hover message) &7:D", null, "color", "white", "black", "show_text", "gray"),
                () -> parse("[Test Gradient](color=#fff,#000 format=bold,italic Hover message) &7:D", null, "color", "white", "black", "show_text", "bold", "italic", "gray"),
                () -> parse("&#fff-#000&Test Gradient&7No Gradient", null, "color", "white", "black", "gray")
        );
    }

    @Test
    public void testParseRainbow() {
        System.out.println("testParseRainbow");
        Assertions.assertAll(
                () -> parse("[Test Rainbow](color=rainbow)", null, "color"),
                () -> parse("[Test Rainbow](rainbow)", null, "color"),
                () -> parse("[Test Rainbow](rainbow:25)", null, "color"),
                () -> parse("[Test Rainbow](rainbow:240)", null, "color"),
                () -> parse("[Test Rainbow with shadow](rainbow shadow=#00000044)", null, "color", "\"shadow_color\":1140850688"),
                () -> parse("&Rainbow&Rainbow&7 Test", null, "color")
        );
    }
    
    @Test
    public void testReplacing() {
        System.out.println("testReplacing");
        Assertions.assertAll(
                () -> parse("&6Test __%placeholder%__&r =D", new String[] {"placeholder", "value"}, "value", "\"underlined\":true"),
                () -> parse("&6Test __%PlaceHolder%__&r =D", new String[] {"placeholder", "**value**"}, "value", "\"underlined\":true", "**value**"),
                () -> parse("&6Test __%placeholder%__&r =D", new String[] {"PlaceHolder", "&5value"}, "value", "\"underlined\":true", "&5value"),
                () -> parse("&6Test __%placeholder%__&r =D", new String[] {"placeholder", "[value](https://example.com)"}, "value", "\"underlined\":true", "[value](https://example.com)")
        );
    }

    @Test
    public void testReplacingFirst() {
        System.out.println("testReplacing");
        Assertions.assertAll(
                () -> parseReplaceFirst("&6Test __%PlaceHolder%__&r =D", new String[] {"placeholder", "**value**"}, "value", "\"underlined\":true", "bold"),
                () -> parseReplaceFirst("&6Test __%placeholder%__&r =D", new String[] {"PlaceHolder", "&5value"}, "value", "\"underlined\":true", "dark_purple"),
                () -> parseReplaceFirst("&6Test %placeholder%&r =D", new String[] {"placeholder", "[value](https://example.com)"}, "value", "open_url", "https://example.com")
        );
    }

    @Test
    public void testComponentReplacing() {
        System.out.println("testComponentReplacing");
        Assertions.assertAll(
                () -> parse("&6Test No placeholder =D", "placeholder", new MineDown("value").toComponent(), "placeholder"),
                () -> parse("&6Test __%placeholder%__&r =D", "placeholder", new MineDown("**value**").toComponent(), "value"),
                () -> parse("&6Test __%PlaceHolder%__&r %placeholder% =D", "placeholder", new MineDown("&5value").toComponent(), "value"),
                () -> parse("&6Test __%placeholder1%__&r %placeholder2%=D",
                        "PlaceHolder1", new MineDown("[replacement1](https://example.com)").toComponent(),
                        "placeholder2", new MineDown("[replacement2](https://example.com)").toComponent(),
                        "replacement1",
                        "replacement2",
                        "https://example.com",
                        "open_url"
                ),
                () -> parse("[Test URL](%placeholder%)", "placeholder", new MineDown("https://example.com").toComponent(), "https://example.com", "open_url"),
                () -> parse("[Test custom payload](custom=my-custom-payload payload={custom-%placeholder%-value})", "placeholder", new MineDown("replaced").toComponent(), "replaced", "\"payload\"")
        );
    }

    @Test
    public void testNegated() {
        Assertions.assertAll(
                () -> parse("&lBold [not bold](!bold) bold", null, "\"bold\":true", "\"bold\":false")
        );
    }

    @Test
    public void testParseNested() {
        Assertions.assertAll(
                () -> parse("[outer start [inner](green) outer end](aqua)", null, "green", "aqua"),
                () -> parse("[outer start \\[[inner](green)\\] outer end](aqua)", null, "green", "aqua", "[", "\"text\":\"]", "[\"}"),
                () -> parse("[outer start [inner](green) outer end](aqua hover={[red hover](red)})", null, "green", "aqua", "show_text", "red hover", "red")
        );
    }

    @Test
    public void testEmptyEvent() {
        Assertions.assertAll(
                () -> parse("[test]()", null)
        );
    }

    @Test
    public void testParseTranslatable() {
        Assertions.assertAll(
                () -> parse("[fallback text](translate=translatable.translation)", null, "translate", "translatable.translation"),
                () -> parse("[fallback text](translate=translatable.translation with={Argument 1,Argument 2})", null, "translate", "translatable.translation", "with", "Argument 1", "Argument 2"),
                () -> parse("[fallback text](translate=translatable.translation with={Argument 1,Argument 2} hover=[hover text](red))", null, "translate", "translatable.translation", "with", "Argument 1", "Argument 2", "red", "show_text"),
                () -> parse("[fallback text](translate=translatable.translation with={Argument 1,Argument 2} hover=[hover text](red) click=open_url=https://example.com)", null, "translate", "translatable.translation", "with", "Argument 1", "Argument 2", "open_url")
        );
    }

    @Test
    public void testParsePlayerHead() {
        Assertions.assertAll(
                () -> parse("Object player head test[](player_head=89d139ff-d454-4488-adfd-127665407cf9)", null, "player", "\"id\""),
                () -> parse("Object player head test[](player_head=TestName)", null, "player", "TestName"),
                () -> parse("Object player head test[](player_head=83688181-ce68-4136-918b-15e88ec2c705 hat=false)", null, "player", "\"hat\":false", "\"id\""),
                () -> parse("Object player head test[](player_head=entity/player/wide/alex)", null, "player", "texture", "minecraft:entity/player/wide/alex"),
                () -> parse("Object player head test[](player_head=minecraft:entity/player/wide/alex)", null, "player", "texture", "minecraft:entity/player/wide/alex"),
            () -> parse("Object player head test[](player_head=TestName texture=minecraft:entity/player/wide/alex)", null, "player", "TestName", "texture", "minecraft:entity/player/wide/alex"),
                () -> parse("Object player head test[](player_head=eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzViNmU0MWY2NmExNzBlYTIzZTg1YjI3NDk2OTRlMjUyNTA2MTgyMTY4NmNiYjFmNjE1Y2VhODEwMmRiYTRmYyJ9fX0=)", null, "player", "properties", "textures", "value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzViNmU0MWY2NmExNzBlYTIzZTg1YjI3NDk2OTRlMjUyNTA2MTgyMTY4NmNiYjFmNjE1Y2VhODEwMmRiYTRmYyJ9fX0"),
                () -> parse("Object player head test[](player_head=TestName profile={textures=eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzViNmU0MWY2NmExNzBlYTIzZTg1YjI3NDk2OTRlMjUyNTA2MTgyMTY4NmNiYjFmNjE1Y2VhODEwMmRiYTRmYyJ9fX0=})", null, "player", "properties", "textures", "value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzViNmU0MWY2NmExNzBlYTIzZTg1YjI3NDk2OTRlMjUyNTA2MTgyMTY4NmNiYjFmNjE1Y2VhODEwMmRiYTRmYyJ9fX0"),
                () -> parse("Object player head test[](player_head=TestName profile={textures=eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzViNmU0MWY2NmExNzBlYTIzZTg1YjI3NDk2OTRlMjUyNTA2MTgyMTY4NmNiYjFmNjE1Y2VhODEwMmRiYTRmYyJ9fX0=,signature=thisisarandomsignature})", null, "TestName", "player", "properties", "textures", "value", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzViNmU0MWY2NmExNzBlYTIzZTg1YjI3NDk2OTRlMjUyNTA2MTgyMTY4NmNiYjFmNjE1Y2VhODEwMmRiYTRmYyJ9fX0")
        );
    }

    @Test
    public void testParseSprite() {
        Assertions.assertAll(
                () -> parse("Object block test[](sprite=stone)", null, "sprite", "stone"),
                () -> parse("Object item test[](sprite=diamond)", null, "sprite", "diamond"),
                () -> parse("Object item test[](sprite=inventory atlas=gui)", null, "sprite", "inventory", "gui", "atlas")
        );
    }
}
