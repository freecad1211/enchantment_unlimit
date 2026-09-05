# ⚔️ enchantment_unlimit

인챈트 레벨 제한을 **없애는** Paper 플러그인. Minecraft **1.21**용.

관리자가 임의의 인챈트를 **레벨 제한 없이** 플레이어 아이템에 적용할 수 있게 해줍니다.

> ⚠️ **주의:** `src/`에는 모루(anvil) 처리용 `AnvilListener.java`가 있으나, 이 리스너는 `onEnable()`에 등록되지 않고 **컴파일도 되지 않는 코드**(존재하지 않는 `Enchantment_unlimit` 클래스 참조)입니다. 현재 실제로 동작하는 기능은 아래 명령어뿐입니다.

## ✨ 실제 동작 기능

- **게임 내 인챈트 부여** — 특정 플레이어의 손에 든 아이템에 원하는 인챈트/레벨 부여 (`addUnsafeEnchantment`, 바닐라 상한 무시)
- **커스텀 최대 레벨** — `config.yml`의 `max-levels` 섹션으로 인챈트별 최대 레벨 지정
- **설정 핫리로드** — `/eu reload` 로 설정 즉시 반영
- **탭 자동완성** — 플레이어명 / 인챈트 ID / 레벨 자동완성 지원 (인챈트명은 `sharpness` 또는 `minecraft:sharpness` 형식)

## 🛠 요구사항

- **Minecraft:** 1.21 (Paper/Purpur)
- **Java:** 21
- **빌드:** Maven

## 💾 설치 / 빌드

```bash
mvn clean package
```

생성된 `target/*.jar` 를 서버 `plugins/` 폴더에 넣고 서버를 재시작합니다.

## 🕹 명령어

| 명령어 | 설명 | 권한 |
|--------|------|------|
| `/unlimitenchant <닉네임> <인챈트> <레벨>` `(/ue)` | 대상 플레이어 손에 든 아이템에 인챈트 적용 | `enchantment_unlimit.admin` |
| `/enchantment_unlimit reload` `(/eu)` | 플러그인 설정 리로드 | `enchantment_unlimit.admin` |

**권한:** `enchantment_unlimit.admin` — 기본값 `op`

## 🔧 config.yml

`max-levels` 섹션으로 인챈트별 커스텀 최대 레벨을 설정합니다. 설정하지 않은 인챈트는 바닐라 최대 레벨을 따릅니다.

```yaml
max-levels:
  sharpness: 10   # 날카로움
  protection: 6   # 보호
  #unbreaking: 5  # 주석 예시
```

## 📁 소스 구조

```
src/main/java/io/github/freecad1211/enchantment_unlimit/
├── EnchantmentUnlimit.java   # 메인 플러그인 클래스 (onEnable, reload, config 로드)
├── EnchantCommand.java       # /ue 명령어 실행 + 탭 자동완성
└── AnvilListener.java        # ⚠️ 미등록·컴파일 불가 (미완성 코드)
```

> 버전: plugin.yml `1.1` / pom.xml `1.0-beta` — Paper API 1.21, Maven shade 빌드.