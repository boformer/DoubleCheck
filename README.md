# DoubleCheck
Action Confirmation Library for Sponge Plugins

## Usage

The DoubleCheck artifacts can be found in my Maven repository:

URL | http://homepage.rub.de/Felix.Schmidt-c2n/maven/
Group | com.github.boformer
ArtifactId | doublecheck
Latest version | 0.2.0-SNAPSHOT

### Gradle
This is a minimal plugin build script (``build.gradle``) that adds the SpongeAPI and DoubleCheck dependencies:
```
apply plugin: 'java'

repositories {
	maven {
		name 'Sponge maven repo'
		url 'http://repo.spongepowered.org/maven'
	}
	maven {
		name = 'boformer maven repo'
		url = 'http://homepage.rub.de/Felix.Schmidt-c2n/maven/'
	}
	mavenCentral()
}

// Add 'provided' dependency configuration
configurations {
	provided
	compile.extendsFrom provided
}

dependencies {
	provided 'org.spongepowered:spongeapi:1.1-SNAPSHOT'
	runtime 'com.github.boformer:doublecheck:0.2.0-SNAPSHOT'
}

// Will include the runtime dependencies in your plugin jar
jar {
	dependsOn configurations.runtime
	from {
		(configurations.runtime - configurations.provided).collect {
			it.isDirectory() ? it : zipTree(it)
		}
	}
}
```

## Example
```java
import java.util.Collections;
import java.util.List;

import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.event.Subscribe;

import com.github.boformer.doublecheck.ConfirmationService;
import com.github.boformer.doublecheck.DoubleCheck;
import com.github.boformer.doublecheck.Request;
import com.google.inject.Inject;

@Plugin(id = "spongetest", name = "SpongeTest", version = "0.1.0")
public class SpongeTestPlugin {

    @Inject
    private Game game;
    
    // The DoubleCheck service class
    private ConfirmationService service;

    @Subscribe
    private void onPreInitialization(PreInitializationEvent event) {

        // Get a new service instance
        service = DoubleCheck.initializeService(game, this);
        
        game.getCommandDispatcher().register(this, new ExampleCommand(), "test");
    }

    private class ExampleCommand implements CommandCallable {

        @Override
        public boolean call(CommandSource source, String arguments, List<String> parents) throws CommandException {
            source.sendMessage(Texts.of("Sending you a request..."));
            service.send(source, new ExampleRequest());
            return true;
        }
        @Override
        public boolean testPermission(CommandSource source) {
            return true;
        }
        @Override
        public String getShortDescription(CommandSource source) {
            return "Test Command";
        }
        @Override
        public Text getHelp(CommandSource source) {
            return Texts.of("Test Command.");
        }
        @Override
        public String getUsage(CommandSource source) {
            return "";
        }
        @Override
        public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
            return Collections.emptyList();
        }

    }
    
    private class ExampleRequest implements Request {

        @Override
        public Text getMessage() {
            return Texts.of("Do you want to know what the magic number is?");
        }
        @Override
        public void confirm(CommandSource source) {
            source.sendMessage(Texts.of("42"));
        }
        @Override
        public void deny(CommandSource source) {
            source.sendMessage(Texts.of("HA HA, you will never know!"));
        }
        
    }
    
}
```
