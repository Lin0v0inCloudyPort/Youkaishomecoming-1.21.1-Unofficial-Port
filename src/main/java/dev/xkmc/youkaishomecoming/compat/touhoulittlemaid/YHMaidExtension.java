package dev.xkmc.youkaishomecoming.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;

public class YHMaidExtension implements ILittleMaid {

    @Override
    public void addMaidTask(TaskManager manager) {
        manager.add(new MaidDanmakuTask());
    }

}
