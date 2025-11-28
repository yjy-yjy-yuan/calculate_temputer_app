## 天气数据与接口说明（学习版，无商用接口）

- 当前版本已改为 **本地模拟数据**，不再调用任何商用/公网接口，便于学习与离线演示。
- `WeatherFragment` 内的 `MockWeatherProvider` 生成随机天气信息；无网络、无 Key 依赖。

### 如果你想自己接入真实接口（可选，需自行申请）
- 在 `local.properties` 写入虚拟占位值即可看到填入位置示例：
  ```
  QWEATHER_API_KEY=123456_DEMO_KEY
  CUSTOM_WEATHER_HOST=https://example.com/api
  ```
- 若要接入真实接口，可将上面占位值替换为你申请到的 Key/URL，并在代码中替换 `MockWeatherProvider` 为实际请求逻辑。
