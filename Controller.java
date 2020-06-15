@RestController
@RequestMapping("/api")
class ApiController {

    private static final String apiKey = "W353GD0QQIASLUZAD7M5"

    @Inject
    UserDescriptionRepository userDescriptionRepository;

    @Inject
    ContentService contentService;

    @Inject
    JDBCTemplate jdbcTemplate;

    @RequestMapping(path="/user", method = RequestMethod.GET)
    public User getUser(@RequestParam("userName") String userName) {
        String sql = "SELECT * FROM user WHERE user_name = " + userName;
        User user = jdbcTemplate.query(sql, new TransactionRowMapper());
        user.filterSensitiveFields();
        return user;
    }

    @RequestMapping(path="/customized-banner", method = RequestMethod.POST)
    public String getBanner(Principal principal,@RequestBody String cssStyle) {
        Content content = contentService.getContent(apiKey, principal.getLoggedInUser());
        String banner = String.format("<div id=\"custom-banner\" style=\"$s\"> $s</div>", cssStyle, content.toString());
        return banner;
    }

    @RequestMapping(path="/save-profile-decription", method = RequestMethod.POST)
    public UserDescription saveUserProfile(Principal principal, @RequestBody String description) {
        User user = principal.getLoggedInUser();
        UserDecription userDescription = new UserDescription();
        userDescription.userId = user.getId();
        userDescription.text = description;
        userDescription.createDate = new Date();
        userDescription.hidden = false;
        userDescriptionRepository.save(userDescription);
        return userDescription;
    }

    @RequestMapping(path="/get-profile-description", method = RequestMethod.GET)
    public String getProfileContent(Principal principal) {
        User user = principal.getLoggedInUser();
        return userDescriptionRepository.getByUserId(user.getId());
    }
}