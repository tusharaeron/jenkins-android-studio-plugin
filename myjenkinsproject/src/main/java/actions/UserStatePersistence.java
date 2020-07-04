package actions;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "demo", storages = @Storage(file = "demo.xml"))
public class UserStatePersistence implements PersistentStateComponent<MyState> {

    private MyState pluginState = new MyState();

    @Nullable
    @Override
    public MyState getState() {
        return pluginState;
    }

    @Override
    public void loadState(@NotNull MyState state) {
        pluginState = state;
    }

    public static PersistentStateComponent<MyState> getInstance() {
        return ServiceManager.getService(UserStatePersistence.class);
    }

    @Override
    public void noStateLoaded() {

    }

    @Override
    public void initializeComponent() {

    }
}
