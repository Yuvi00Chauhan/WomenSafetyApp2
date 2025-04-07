import com.shubham.womensafety.database.Guardian
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.shubham.womensafety.databinding.ItemGuardianBinding

class GuardianAdapter(private var guardians: List<Guardian>) : RecyclerView.Adapter<GuardianAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGuardianBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(guardians[position])
    }

    override fun getItemCount(): Int = guardians.size

    fun updateData(newGuardians: List<Guardian>) {
        guardians = newGuardians
        notifyDataSetChanged() // Notify adapter about data changes
    }

    class ViewHolder(private val binding: ItemGuardianBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(guardian: Guardian) {
            binding.textName.text = guardian.guardianName
            binding.textRelation.text = guardian.guardianRelation
            binding.textPhone.text = guardian.guardianPhoneNo
            binding.textEmail.text = guardian.guardianEmail
        }
    }
}
